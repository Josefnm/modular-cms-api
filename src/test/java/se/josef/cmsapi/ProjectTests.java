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
import se.josef.cmsapi.config.SecurityAuditorAware;
import se.josef.cmsapi.model.document.Project;
import se.josef.cmsapi.model.web.ProjectForm;
import se.josef.cmsapi.model.web.ProjectUpdateForm;
import se.josef.cmsapi.repository.ProjectRepository;
import se.josef.cmsapi.service.UserService;
import se.josef.cmsapi.util.UserUtils;
import se.josef.cmsapi.utils.RequestUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.*;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static se.josef.cmsapi.utils.MockDataUtil.getNewProject;

@Slf4j
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration(exclude = {SecurityAutoConfiguration.class})
public class ProjectTests {

    private static final String PROJECT_ID_1 = new ObjectId().toString();
    private static final String PROJECT_ID_2 = new ObjectId().toString();
    private static final String PROJECT_ID_3 = new ObjectId().toString();

    private static final String PROJECT_NAME_1 = "my project";
    private static final String PROJECT_DESCRIPTION_1 = "this is a project that is and else or yes no hello";
    private static final String UPDATED_PROJECT_NAME_1 = "updated project name";
    private static final String UPDATED_PROJECT_DESCRIPTION_1 = "updated description";

    private static final String PROJECT_NAME_2 = "my second project";
    private static final String PROJECT_DESCRIPTION_2 = "project description2";

    private static final String USER_ID_1 = randomAlphanumeric(24);
    private static final String USER_ID_2 = randomAlphanumeric(24);
    private static final String USER_ID_3 = randomAlphanumeric(24);


    @LocalServerPort
    private int port;
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
    class WithTeardownBetweenEach {

        @AfterEach
        void dropDB() {
            mongoTemplate.getDb().drop();
        }

        @BeforeEach
        void saveDefaultProject() {
            var project = Project.builder()
                    .id(PROJECT_ID_1)
                    .ownerId(USER_ID_1)
                    .memberIds(Collections.singletonList(USER_ID_1))
                    .name(PROJECT_NAME_1)
                    .description(PROJECT_DESCRIPTION_1)
                    .build();
            projectRepository.save(project);
        }

        @Test
        public void saveProjectSuccess() {
            var projectForm = new ProjectForm(PROJECT_NAME_2, PROJECT_DESCRIPTION_2);

            var response = requestUtils.postRequest("/project", Project.class, projectForm, port);

            assertEquals(PROJECT_NAME_2, Objects.requireNonNull(response.getBody()).getName());
            assertEquals(PROJECT_DESCRIPTION_2, response.getBody().getDescription());
            assertEquals(USER_ID_1, response.getBody().getOwnerId());
            assertEquals(USER_ID_1, response.getBody().getMemberIds().get(0));
        }

        @Test
        public void updateProjectSuccess() {
            var members = new ArrayList<String>();
            members.add(USER_ID_1);
            members.add(randomAlphanumeric(24));


            var projectForm = new ProjectUpdateForm(PROJECT_ID_1, members, UPDATED_PROJECT_NAME_1, UPDATED_PROJECT_DESCRIPTION_1);

            var response = requestUtils.postRequest("/project/update", Project.class, projectForm, port);

            assertEquals(UPDATED_PROJECT_NAME_1, Objects.requireNonNull(response.getBody()).getName());
            assertEquals(UPDATED_PROJECT_DESCRIPTION_1, response.getBody().getDescription());
            assertEquals(USER_ID_1, response.getBody().getOwnerId());
            assertEquals(2, response.getBody().getMemberIds().size());
        }

        @Test
        public void deleteProjectSuccess() {
            assertDoesNotThrow(() -> requestUtils.deleteRequest("/project/" + PROJECT_ID_1, port));
        }

    }

    @Nested
    class WithSameData {

        @PostConstruct
        void setMockOutputAndDatabase() {
            var projects = List.of(
                    getNewProject(PROJECT_ID_1, USER_ID_1, USER_ID_2),
                    getNewProject(PROJECT_ID_2, USER_ID_2, USER_ID_1),
                    getNewProject(PROJECT_ID_3, USER_ID_2, USER_ID_3)
            );
            projectRepository.saveAll(projects);
        }

        @Test
        public void getProjectByIdSuccess() {
            var response = requestUtils.getRequest("/project/" + PROJECT_ID_1, Project.class, port);

            assertEquals(PROJECT_ID_1, Objects.requireNonNull(response.getBody()).getId());
            assertEquals(USER_ID_1, response.getBody().getOwnerId());
        }

        @Test
        public void getProjectByIdSuccess2() {
            var response = requestUtils.getRequest("/project/" + PROJECT_ID_2, Project.class, port);

            assertEquals(PROJECT_ID_2, Objects.requireNonNull(response.getBody()).getId());
            assertEquals(USER_ID_2, response.getBody().getOwnerId());
        }

        @Test
        public void getProjectByIdFail() {
            var response = requestUtils.getRequest("/project/" + PROJECT_ID_3, Project.class, port);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }

        @Test
        public void getProjectsForCurrentUser() {
            var response = requestUtils.getRequest("/project/user", Project[].class, port);

            assertEquals(2, Objects.requireNonNull(response.getBody()).length);
        }
    }
}
