package se.josef.cmsapi;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static se.josef.cmsapi.utils.MockDataUtil.*;


@Slf4j
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration(exclude = SecurityAutoConfiguration.class)
public class TemplateTests {

    private static final String templateId = getRandomLowercaseNumeric(24);
    private static final String userId = getRandomLowercaseNumeric(24);
    private static final String projectId = getRandomLowercaseNumeric(24);
    private static final String templateName1 = "my test template";
    private static final String templateName2 = "my best template";
    private static final String templateName3 = "my bezt template";
    private static final String searchString1 = "est";
    private static final String searchString2 = "template";

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
        // extra drop for redundancy
        mongoTemplate.getDb().drop();
        doReturn(userId).when(userUtils).getUserId();
        doReturn(Optional.of(userId)).when(securityAuditorAware).getCurrentAuditor();
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
            var templateForm = new TemplateForm(projectId, getRandomAlphaNumeric(12), getRandomAlphaNumericAndSymbols(100), new ArrayList<>());
            var response = requestUtils.postRequest("/template", Template.class, templateForm, port);

            assertEquals(userId, Objects.requireNonNull(response.getBody()).getOwnerId());
        }
    }

    @Nested
    class WithData {
        @PostConstruct
        void setMockOutputAndDatabase() {
            var project = getNewProject(projectId, userId);
            var template1 = getNewTemplate(templateId, projectId, templateName1);
            var template2 = getNewTemplate(getRandomLowercaseNumeric(24), projectId, templateName2);
            var template3 = getNewTemplate(getRandomLowercaseNumeric(24), projectId, templateName3);
            var template4 = getNewTemplate(getRandomLowercaseNumeric(24), getRandomLowercaseNumeric(24), templateName1);

            var projectFuture = CompletableFuture.runAsync(() -> projectRepository.save(project));
            var templateFuture1 = CompletableFuture.runAsync(() -> templateRepository.save(template1));
            var templateFuture2 = CompletableFuture.runAsync(() -> templateRepository.save(template2));
            var templateFuture3 = CompletableFuture.runAsync(() -> templateRepository.save(template3));
            var templateFuture4 = CompletableFuture.runAsync(() -> templateRepository.save(template4));

            CompletableFuture.allOf(projectFuture, templateFuture1, templateFuture2, templateFuture3, templateFuture4).join();
        }

        @Test
        public void getTemplatesByProjectIdSuccess() {
            var response = requestUtils.getRequest("/template/projectId/" + projectId + "", Template[].class, port);

            assertEquals(3, Objects.requireNonNull(response.getBody()).length);
        }

        @Test
        public void getTemplateByProjectIdFail() {
            assertThrows(RestClientException.class,
                    () -> requestUtils.getRequest("/template/projectId/" + getRandomLowercaseNumeric(24),
                            Template[].class,
                            port)
            );
        }

        @Test
        public void getTemplateByIdSuccess() {
            var response = requestUtils.getRequest("/template/" + templateId, Template.class, port);

            assertEquals(templateId, Objects.requireNonNull(response.getBody()).getId());
        }

        @Test
        public void getTemplatesByIdFail() {
            assertThrows(RestClientException.class,
                    () -> requestUtils.getRequest("/template/projectId/" + getRandomLowercaseNumeric(24),
                            Template[].class,
                            port)
            );
        }

        @Test
        public void searchTemplateByNameAndProjectIdSuccess1() {
            String uri = String.format("/template/search/%s?searchString=%s", projectId, searchString1);
            var response = requestUtils.getRequest(uri, Template[].class, port);

            assertEquals(2, Objects.requireNonNull(response.getBody()).length);
        }

        @Test
        public void searchTemplateByNameAndProjectIdSuccess2() {
            var uri = String.format("/template/search/%s?searchString=%s", projectId, searchString2);
            var response = requestUtils.getRequest(uri, Template[].class, port);

            assertEquals(3, Objects.requireNonNull(response.getBody()).length);
        }

        @Test
        public void searchTemplateByNameAndProjectIdNoResults() {
            var uri = String.format("/template/search/%s?searchString=%s", projectId, getRandomLowercaseNumeric(8));
            var response = requestUtils.getRequest(uri, Template[].class, port);

            assertEquals(0, Objects.requireNonNull(response.getBody()).length);
        }

        @Test
        public void searchTemplateByNameAndProjectIdEmptyString() {
            var uri = String.format("/template/search/%s?searchString=", projectId);
            var response = requestUtils.getRequest(uri, Template[].class, port);

            assertEquals(3, Objects.requireNonNull(response.getBody()).length);
        }

        @Test
        public void searchTemplateByNameAndProjectIdThrows() {
            var uri = String.format("/template/search/%s?searchString=%s", getRandomLowercaseNumeric(24), searchString2);

            assertThrows(RestClientException.class,
                    () -> requestUtils.getRequest(uri,
                            Template[].class,
                            port)
            );
        }
    }

}
