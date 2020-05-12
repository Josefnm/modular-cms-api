package se.josef.cmsapi;

import com.google.firebase.auth.FirebaseAuthException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
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
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

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

    private static final String projectId = getRandomLowercaseNumeric(24);
    private static final String userId1 = getRandomLowercaseNumeric(24);
    private static final String userName1 = "josef josefsson";
    private static final String email1 = "josef@josef.josef";
    private static final String userId2 = getRandomLowercaseNumeric(24);
    private static final String userName2 = "Göran Göransson";
    private static final String email2 = "goran@goransson.se";
    private static final String userId3 = getRandomLowercaseNumeric(24);
    private static final String userName3 = "Ander Andersson";
    private static final String email3 = "fsdfs@fdsfdsf.com";
    private static final String searchString1 = "son";
    private static final String searchString2 = "josef";

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

    @PostConstruct
    void setSecurityContextUser() throws FirebaseAuthException {
        // extra drop for redundancy
        mongoTemplate.getDb().drop();
        doReturn(userId1).when(userService).saveToFirebase(any());
        doReturn(userId1).when(userUtils).getUserId();
        doReturn(Optional.of(userId1)).when(securityAuditorAware).getCurrentAuditor();
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
            userRepository.save(getNewUser(userId1, userName1, email1));

        }

        @Test
        public void signupSuccess() {
            var userForm = new UserForm("name", "email@email.com", "password");

            var response = requestUtils.postRequest("/user/signup", User.class, userForm, port);
            log.info(response.toString());
            assertEquals("name", Objects.requireNonNull(response.getBody()).getUserName());
        }

        @Test
        public void signupFailSameName() {
            var userForm = new UserForm(userName1, "another@email.se", "password");

            var response = requestUtils.postRequest("/user/signup", User.class, userForm, port);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }

        @Test
        public void signupFailSameEmail() {
            var userForm = new UserForm(getRandomLowercaseNumeric(12), email1, "password");

            var response = requestUtils.postRequest("/user/signup", User.class, userForm, port);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }
    }

    @Nested
    class WithData {
        @PostConstruct
        void setMockOutputAndDatabase() {
            var project = getNewProject(projectId, userId1, userId2);
            var user1 = getNewUser(userId1, userName1, email1);
            var user2 = getNewUser(userId2, userName2, email2);
            var user3 = getNewUser(userId3, userName3, email3);

            var projectFuture = CompletableFuture.runAsync(() -> projectRepository.save(project));
            var userFuture1 = CompletableFuture.runAsync(() -> userRepository.save(user1));
            var userFuture2 = CompletableFuture.runAsync(() -> userRepository.save(user2));
            var userFuture3 = CompletableFuture.runAsync(() -> userRepository.save(user3));

            CompletableFuture.allOf(userFuture1, userFuture2, userFuture3, projectFuture).join();
        }

        @Test
        public void getCurrentUserSuccess() {
            var response = requestUtils.getRequest("/user", User.class, port);

            assertEquals(userId1, Objects.requireNonNull(response.getBody()).getId());
        }

        @Test
        public void getUserByIdSuccess() {
            var response = requestUtils.getRequest("/user/" + userId1, User.class, port);

            assertEquals(userId1, Objects.requireNonNull(response.getBody()).getId());
        }

        @Test
        public void getUserByIdBadRequest() {
            var response = requestUtils.getRequest("/user/" + getRandomLowercaseNumeric(24), User.class, port);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }

        @Test
        public void getAllUsersSuccess() {
            var response = requestUtils.getRequest("/user/all", User[].class, port);

            assertEquals(3, Objects.requireNonNull(response.getBody()).length);
        }

        @Test
        public void searchUsersSuccess1() {
            var uri = String.format("/user/search?searchString=%s&projectId=%s", searchString1, projectId);
            log.info(uri);
            var response = requestUtils.getRequest(uri, User[].class, port);
            assertEquals(1, Objects.requireNonNull(response.getBody()).length);
        }

        @Test
        public void searchUsersSuccess2() {
            var uri = String.format("/user/search?searchString=%s&projectId=%s", searchString2, projectId);
            var response = requestUtils.getRequest(uri, User[].class, port);
            assertEquals(0, Objects.requireNonNull(response.getBody()).length);
        }

        @Test
        public void getUsersByProjectSuccess() {
            var uri = String.format("/user/project?projectId=%s", projectId);
            var response = requestUtils.getRequest(uri, User[].class, port);
            assertEquals(2, Objects.requireNonNull(response.getBody()).length);
        }

        @Test
        public void getUsersByProjectThrows() {
            var uri = String.format("/user/project?projectId=%s", getRandomLowercaseNumeric(24));

            assertThrows(RestClientException.class, () -> requestUtils.getRequest(uri, User[].class, port));
        }

    }
}
