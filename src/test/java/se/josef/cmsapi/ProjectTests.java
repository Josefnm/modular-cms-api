package se.josef.cmsapi;

import com.google.firebase.auth.FirebaseAuthException;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
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
import se.josef.cmsapi.config.SecurityAuditorAware;
import se.josef.cmsapi.model.document.Project;
import se.josef.cmsapi.model.web.ProjectForm;
import se.josef.cmsapi.repository.ProjectRepository;
import se.josef.cmsapi.service.UserService;
import se.josef.cmsapi.util.UserUtils;
import se.josef.cmsapi.utils.RequestUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static se.josef.cmsapi.utils.MockDataUtil.getNewProject;
import static se.josef.cmsapi.utils.MockDataUtil.getRandomLowercaseNumeric;

@Slf4j
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration(exclude = {SecurityAutoConfiguration.class})
public class ProjectTests {

    private static final String projectId1 = new ObjectId().toString();
    private static final String projectId2 = new ObjectId().toString();
    private static final String projectId3 = new ObjectId().toString();

    private static final String projectName1 = "my project";
    private static final String projectDescription1 = "this is a project that is and else or yes no hello";
    private static final String updatedProjectName1 = "updated project name";
    private static final String updatedProjectDescription1 = "updated description";

    private static final String projectName2 = "my second project";
    private static final String projectDescription2 = "project description2";

    private static final String userId1 = getRandomLowercaseNumeric(24);
    private static final String userId2 = getRandomLowercaseNumeric(24);
    private static final String userId3 = getRandomLowercaseNumeric(24);


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
        doReturn(userId1).when(userService).saveToFirebase(any());
        doReturn(userId1).when(userUtils).getUserId();
        doReturn(Optional.of(userId1)).when(securityAuditorAware).getCurrentAuditor();
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
                    .id(projectId1)
                    .ownerId(userId1)
                    .memberIds(Collections.singletonList(userId1))
                    .name(projectName1)
                    .description(projectDescription1)
                    .build();
            projectRepository.save(project);
        }

        @Test
        public void saveProjectSuccess() {
            var projectForm = new ProjectForm(null, userId1, null, projectName2, projectDescription2);

            var response = requestUtils.postRequest("/project", Project.class, projectForm, port);

            assertEquals(projectName2, Objects.requireNonNull(response.getBody()).getName());
            assertEquals(projectDescription2, response.getBody().getDescription());
            assertEquals(userId1, response.getBody().getOwnerId());
            assertEquals(userId1, response.getBody().getMemberIds().get(0));
        }

        @Test
        public void updateProjectSuccess() {
            var members = new ArrayList<String>();
            members.add(userId1);
            members.add(getRandomLowercaseNumeric(24));


            var projectForm = new ProjectForm(projectId1, userId1, members, updatedProjectName1, updatedProjectDescription1);

            var response = requestUtils.postRequest("/project/update", Project.class, projectForm, port);

            assertEquals(updatedProjectName1, Objects.requireNonNull(response.getBody()).getName());
            assertEquals(updatedProjectDescription1, response.getBody().getDescription());
            assertEquals(userId1, response.getBody().getOwnerId());
            assertEquals(2, response.getBody().getMemberIds().size());
        }

        @Test
        public void deleteProjectSuccess() {
            assertDoesNotThrow(() -> requestUtils.deleteRequest("/project/" + projectId1, port));
        }

    }

    @Nested
    class WithSameData {

        @PostConstruct
        void setMockOutputAndDatabase() {
            var project1 = getNewProject(projectId1, userId1, userId2);
            var project2 = getNewProject(projectId2, userId2, userId1);
            var project3 = getNewProject(projectId3, userId2, userId3);

            var projectFuture1 = CompletableFuture.runAsync(() -> projectRepository.save(project1));
            var projectFuture2 = CompletableFuture.runAsync(() -> projectRepository.save(project2));
            var projectFuture3 = CompletableFuture.runAsync(() -> projectRepository.save(project3));

            CompletableFuture.allOf(projectFuture1, projectFuture2, projectFuture3).join();
        }

        @Test
        public void getProjectByIdSuccess() {
            var response = requestUtils.getRequest("/project/" + projectId1, Project.class, port);

            assertEquals(projectId1, Objects.requireNonNull(response.getBody()).getId());
            assertEquals(userId1, response.getBody().getOwnerId());
        }

        @Test
        public void getProjectByIdSuccess2() {
            var response = requestUtils.getRequest("/project/" + projectId2, Project.class, port);

            assertEquals(projectId2, Objects.requireNonNull(response.getBody()).getId());
            assertEquals(userId2, response.getBody().getOwnerId());
        }

        @Test
        public void getProjectByIdFail() {
            var response = requestUtils.getRequest("/project/" + projectId3, Project.class, port);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }

        @Test
        public void getProjectsForCurrentUser() {
            var response = requestUtils.getRequest("/project/user", Project[].class, port);

            assertEquals(2, Objects.requireNonNull(response.getBody()).length);
        }
    }
}
