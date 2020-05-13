package se.josef.cmsapi;

import com.google.firebase.auth.FirebaseAuthException;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClientException;
import se.josef.cmsapi.config.SecurityAuditorAware;
import se.josef.cmsapi.model.document.User;
import se.josef.cmsapi.model.web.UserForm;
import se.josef.cmsapi.repository.ProjectRepository;
import se.josef.cmsapi.repository.UserRepository;
import se.josef.cmsapi.service.UserService;
import se.josef.cmsapi.util.UserUtils;
import se.josef.cmsapi.utils.RequestUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static java.util.Objects.requireNonNull;
import static java.util.concurrent.CompletableFuture.runAsync;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static se.josef.cmsapi.utils.MockDataUtil.*;

@Slf4j
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration(exclude = {SecurityAutoConfiguration.class})
public class UserTests {

    private static final String PROJECT_ID = new ObjectId().toString();
    private static final String USER_ID_1 = randomAlphanumeric(24);
    private static final String USER_NAME_1 = "josef josefsson";
    private static final String EMAIL_1 = "josef@josef.josef";
    private static final String USER_ID_2 = randomAlphanumeric(24);
    private static final String USER_NAME_2 = "Göran Göransson";
    private static final String EMAIL_2 = "goran@goransson.se";
    private static final String USER_ID_3 = randomAlphanumeric(24);
    private static final String USER_NAME_3 = "Ander Andersson";
    private static final String EMAIL_3 = randomEmail();
    private static final String SEARCH_STRING_1 = "rsson";
    private static final String SEARCH_STRING_2 = "josef";

    @LocalServerPort
    private int port;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @SpyBean
    private UserService userService;
    @MockBean
    private UserUtils userUtils;
    @MockBean
    private SecurityAuditorAware securityAuditorAware;
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    private RequestUtils requestUtils;

    /**
     * Drops db for redundancy and sets up mocks
     */
    @PostConstruct
    void setSecurityContextUser() throws FirebaseAuthException {
        mongoTemplate.getDb().drop();
        doReturn(USER_ID_1).when(userService).saveToFirebase(any());
        doReturn(USER_ID_1).when(userUtils).getUserId();
        doReturn(Optional.of(USER_ID_1)).when(securityAuditorAware).getCurrentAuditor();
    }

    @PreDestroy
    void preDestroy() {
        mongoTemplate.getDb().drop();
    }

    @Nested
    class WithTeardown {

        @AfterEach
        void dropDB() {
            mongoTemplate.getDb().drop();
        }

        @BeforeEach
        void beforeSave() {
            userRepository.save(getNewUser(USER_ID_1, USER_NAME_1, EMAIL_1));
        }

        @Test
        public void signupSuccess() {
            var name = randomAlphanumeric(12);
            var userForm = new UserForm(name, randomEmail(), randomAlphabetic(10));

            var response = requestUtils.postRequest("/user/signup", User.class, userForm, port);

            assertEquals(name, requireNonNull(response.getBody()).getUserName());
        }

        @Test
        public void signupFailSameName() {
            var userForm = new UserForm(USER_NAME_1, randomEmail(), randomAlphabetic(10));

            var response = requestUtils.postRequest("/user/signup", User.class, userForm, port);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }

        @Test
        public void signupFailSameEmail() {
            var userForm = new UserForm(randomAlphanumeric(12), EMAIL_1, randomAlphabetic(10));

            var response = requestUtils.postRequest("/user/signup", User.class, userForm, port);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }
    }

    @Nested
    class WithData {
        @PostConstruct
        void setMockOutputAndDatabase() {
            var project = getNewProject(PROJECT_ID, USER_ID_1, USER_ID_2);
            var user1 = getNewUser(USER_ID_1, USER_NAME_1, EMAIL_1);
            var user2 = getNewUser(USER_ID_2, USER_NAME_2, EMAIL_2);
            var user3 = getNewUser(USER_ID_3, USER_NAME_3, EMAIL_3);

            CompletableFuture.allOf(
                    runAsync(() -> projectRepository.save(project)),
                    runAsync(() -> userRepository.save(user1)),
                    runAsync(() -> userRepository.save(user2)),
                    runAsync(() -> userRepository.save(user3)))
                    .join();
        }

        @Test
        public void getCurrentUserSuccess() {
            var response = requestUtils.getRequest("/user", User.class, port);

            assertEquals(USER_ID_1, requireNonNull(response.getBody()).getId());
        }

        @Test
        public void getUserByIdSuccess() {
            var response = requestUtils.getRequest("/user/" + USER_ID_1, User.class, port);

            assertEquals(USER_ID_1, requireNonNull(response.getBody()).getId());
        }

        @Test
        public void getUserByIdBadRequest() {
            var response = requestUtils.getRequest("/user/" + randomAlphanumeric(24), User.class, port);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }

        @Test
        public void getAllUsersSuccess() {
            var response = requestUtils.getRequest("/user/all", User[].class, port);

            assertEquals(3, requireNonNull(response.getBody()).length);
        }

        @Test
        public void searchUsersSuccess1() {
            var uri = String.format("/user/search?searchString=%s&projectId=%s", SEARCH_STRING_1, PROJECT_ID);
            var response = requestUtils.getRequest(uri, User[].class, port);

            assertEquals(1, requireNonNull(response.getBody()).length);
        }

        @Test
        public void searchUsersSuccess2() {
            var uri = String.format("/user/search?searchString=%s&projectId=%s", SEARCH_STRING_2, PROJECT_ID);
            var response = requestUtils.getRequest(uri, User[].class, port);

            assertEquals(0, requireNonNull(response.getBody()).length);
        }

        @Test
        public void getUsersByProjectSuccess() {
            var uri = String.format("/user/project?projectId=%s", PROJECT_ID);
            var response = requestUtils.getRequest(uri, User[].class, port);

            assertEquals(2, requireNonNull(response.getBody()).length);
        }

        @Test
        public void getUsersByProjectThrows() {
            var uri = String.format("/user/project?projectId=%s", randomAlphanumeric(24));

            assertThrows(RestClientException.class, () -> requestUtils.getRequest(uri, User[].class, port));
        }

    }
}
