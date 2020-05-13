package se.josef.cmsapi;

import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClientException;
import se.josef.cmsapi.config.SecurityAuditorAware;
import se.josef.cmsapi.model.document.Template;
import se.josef.cmsapi.model.web.TemplateForm;
import se.josef.cmsapi.repository.ProjectRepository;
import se.josef.cmsapi.repository.TemplateRepository;
import se.josef.cmsapi.util.UserUtils;
import se.josef.cmsapi.utils.RequestUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.runAsync;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.apache.commons.lang3.RandomStringUtils.randomAscii;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static se.josef.cmsapi.utils.MockDataUtil.getNewProject;
import static se.josef.cmsapi.utils.MockDataUtil.getNewTemplate;

@Slf4j
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration(exclude = SecurityAutoConfiguration.class)
public class TemplateTests {

    private static final String TEMPLATE_ID = new ObjectId().toString();
    private static final String USER_ID = randomAlphanumeric(24);
    private static final String PROJECT_ID = new ObjectId().toString();
    private static final String TEMPLATE_NAME_1 = "my test template";
    private static final String TEMPLATE_NAME_2 = "my best template";
    private static final String TEMPLATE_NAME_3 = "my bezt template";
    private static final String SEARCH_STRING_1 = "est";
    private static final String SEARCH_STRING_2 = "template";

    @LocalServerPort
    private int port;
    @Autowired
    private TemplateRepository templateRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @MockBean
    private UserUtils userUtils;
    @MockBean
    private SecurityAuditorAware securityAuditorAware;
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    private RequestUtils requestUtils;

    @PostConstruct
    void setSecurityContextUser() {
        mongoTemplate.getDb().drop();
        doReturn(USER_ID).when(userUtils).getUserId();
        doReturn(Optional.of(USER_ID)).when(securityAuditorAware).getCurrentAuditor();
    }

    @PreDestroy
    void preDestroy() {
        mongoTemplate.getDb().drop();
    }

    @Nested
    class WithTeardown {

        @AfterEach
        void setMockOutputAndDatabase() {
            mongoTemplate.getDb().drop();
        }

        @Test
        public void saveTemplateSuccess() {
            var templateForm = new TemplateForm(PROJECT_ID, randomAlphanumeric(12), randomAscii(100), new ArrayList<>());
            var response = requestUtils.postRequest("/template", Template.class, templateForm, port);

            assertEquals(USER_ID, Objects.requireNonNull(response.getBody()).getOwnerId());
        }
    }

    @Nested
    class WithData {
        @PostConstruct
        void setMockOutputAndDatabase() {
            var project = getNewProject(PROJECT_ID, USER_ID);
            var template1 = getNewTemplate(TEMPLATE_ID, PROJECT_ID, TEMPLATE_NAME_1);
            var template2 = getNewTemplate(randomAlphanumeric(24), PROJECT_ID, TEMPLATE_NAME_2);
            var template3 = getNewTemplate(randomAlphanumeric(24), PROJECT_ID, TEMPLATE_NAME_3);
            var template4 = getNewTemplate(randomAlphanumeric(24), randomAlphanumeric(24), TEMPLATE_NAME_1);

            CompletableFuture.allOf(
                    runAsync(() -> projectRepository.save(project)),
                    runAsync(() -> templateRepository.save(template1)),
                    runAsync(() -> templateRepository.save(template2)),
                    runAsync(() -> templateRepository.save(template3)),
                    runAsync(() -> templateRepository.save(template4)))
                    .join();
        }

        @Test
        public void getTemplatesByProjectIdSuccess() {
            var response = requestUtils.getRequest("/template/projectId/" + PROJECT_ID + "", Template[].class, port);

            assertEquals(3, Objects.requireNonNull(response.getBody()).length);
        }

        @Test
        public void getTemplateByProjectIdFail() {
            assertThrows(RestClientException.class,
                    () -> requestUtils.getRequest("/template/projectId/" + randomAlphanumeric(24),
                            Template[].class,
                            port)
            );
        }

        @Test
        public void getTemplateByIdSuccess() {
            var response = requestUtils.getRequest("/template/" + TEMPLATE_ID, Template.class, port);

            assertEquals(TEMPLATE_ID, Objects.requireNonNull(response.getBody()).getId());
        }

        @Test
        public void getTemplatesByIdFail() {
            assertThrows(RestClientException.class,
                    () -> requestUtils.getRequest("/template/projectId/" + randomAlphanumeric(24),
                            Template[].class,
                            port)
            );
        }

        @Test
        public void searchTemplateByNameAndProjectIdSuccess1() {
            String uri = String.format("/template/search/%s?searchString=%s", PROJECT_ID, SEARCH_STRING_1);
            var response = requestUtils.getRequest(uri, Template[].class, port);

            assertEquals(2, Objects.requireNonNull(response.getBody()).length);
        }

        @Test
        public void searchTemplateByNameAndProjectIdSuccess2() {
            var uri = String.format("/template/search/%s?searchString=%s", PROJECT_ID, SEARCH_STRING_2);
            var response = requestUtils.getRequest(uri, Template[].class, port);

            assertEquals(3, Objects.requireNonNull(response.getBody()).length);
        }

        @Test
        public void searchTemplateByNameAndProjectIdNoResults() {
            var uri = String.format("/template/search/%s?searchString=%s", PROJECT_ID, randomAlphanumeric(8));
            var response = requestUtils.getRequest(uri, Template[].class, port);

            assertEquals(0, Objects.requireNonNull(response.getBody()).length);
        }

        @Test
        public void searchTemplateByNameAndProjectIdEmptyString() {
            var uri = String.format("/template/search/%s?searchString=", PROJECT_ID);
            var response = requestUtils.getRequest(uri, Template[].class, port);

            assertEquals(3, Objects.requireNonNull(response.getBody()).length);
        }

        @Test
        public void searchTemplateByNameAndProjectIdThrows() {
            var uri = String.format("/template/search/%s?searchString=%s", randomAlphanumeric(24), SEARCH_STRING_2);

            assertThrows(RestClientException.class,
                    () -> requestUtils.getRequest(uri,
                            Template[].class,
                            port)
            );
        }
    }

}
