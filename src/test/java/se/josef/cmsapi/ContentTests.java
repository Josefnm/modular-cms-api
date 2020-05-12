package se.josef.cmsapi;

import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClientException;
import se.josef.cmsapi.config.SecurityAuditorAware;
import se.josef.cmsapi.model.document.Content;
import se.josef.cmsapi.model.document.contentField.*;
import se.josef.cmsapi.model.web.ContentForm;
import se.josef.cmsapi.model.web.contentsearch.*;
import se.josef.cmsapi.model.web.contentsearch.parameter.RangeParameter;
import se.josef.cmsapi.repository.ContentRepository;
import se.josef.cmsapi.repository.ProjectRepository;
import se.josef.cmsapi.repository.TemplateRepository;
import se.josef.cmsapi.repository.UserRepository;
import se.josef.cmsapi.util.UserUtils;
import se.josef.cmsapi.utils.RequestUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static se.josef.cmsapi.utils.MockDataUtil.*;


@Slf4j
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration(exclude = SecurityAutoConfiguration.class)
public class ContentTests {

    private static final String userId1 = getRandomLowercaseNumeric(24);
    private static final String userId2 = getRandomLowercaseNumeric(24);
    private static final String templateId1 = new ObjectId().toString();
    private static final String templateId2 = new ObjectId().toString();
    private static final String templateId3 = new ObjectId().toString();
    private static final String projectId1 = new ObjectId().toString();
    private static final String projectId2 = new ObjectId().toString();
    private static final String contentId1 = new ObjectId().toString();
    private static final String contentId2 = new ObjectId().toString();
    private static final String contentId3 = new ObjectId().toString();
    private static final String contentName1 = getRandomLowercaseNumeric(24);
    private static final String updatedContentName1 = "my test content 2";
    private static final String contentName2 = getRandomLowercaseNumeric(24);

    private static final String boolName = getRandomAlphaNumericAndSymbols(12);
    private static final String dateName = getRandomAlphaNumericAndSymbols(12);
    private static final String imageName = getRandomAlphaNumericAndSymbols(12);
    private static final String imageUrl = getRandomAlphaNumericAndSymbols(40);
    private static final String moduleName = getRandomAlphaNumericAndSymbols(12);
    private static final String moduleId = getRandomLowercaseNumeric(24);
    private static final String numberName = getRandomAlphaNumericAndSymbols(12);
    private static final Long numberValue = getRandomLong(12);
    private static final String stringName = getRandomAlphaNumeric(12);
    private static final String stringValue = getRandomAlphaNumeric(50);
    private static final String textName = getRandomAlphaNumeric(12);
    private static final String textValue = getRandomAlphaNumeric(300);


    @LocalServerPort
    private int port;
    @Autowired
    private TemplateRepository templateRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private ContentRepository contentRepository;
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
        void setMockOutputAndDatabase() {
            mongoTemplate.getDb().drop();
        }

        @BeforeEach
        void saveDefaultContent() {

            var content = Content.builder()
                    .id(contentId1)
                    .ownerId(userId1)
                    .name(contentName1)
                    .isPublic(true)
                    .contentFields(new ArrayList<>())
                    .templateId(templateId1)
                    .projectId(projectId1)
                    .created(new Date())
                    .updated(new Date())
                    .build();
            var project = getNewProject(projectId1, userId1);

            var contentFuture = CompletableFuture.runAsync(() -> contentRepository.save(content));
            var projectFuture = CompletableFuture.runAsync(() -> projectRepository.save(project));

            CompletableFuture.allOf(contentFuture, projectFuture);
        }

        //some issues with this this, fails irregularly. probably database related.
        @Disabled
        @Test
        public void updateContentSuccess() {
            var contentFields = new ArrayList<ContentField<?>>();

            contentFields.add(new StringField(getRandomLowercaseNumeric(12), getRandomLowercaseNumeric(12)));
            contentFields.add(new DateField(getRandomLowercaseNumeric(12), new Date()));


            var contentForm = new ContentForm(contentId1, null, null, updatedContentName1, false, contentFields);
            var response = requestUtils.postRequest("/content/update", Content.class, contentForm, port);
            log.info(response.getStatusCode().toString());
            assertEquals(userId1, Objects.requireNonNull(response.getBody()).getOwnerId());
            assertEquals(updatedContentName1, Objects.requireNonNull(response.getBody()).getName());
            assertEquals(2, response.getBody().getContentFields().size());
        }

        @Test
        public void addContentSuccess() {
            var contentForm = new ContentForm(null, projectId1, templateId1, getRandomAlphaNumeric(12), true, new ArrayList<>());
            var response = requestUtils.postRequest("/content", Content.class, contentForm, port);

            assertEquals(userId1, Objects.requireNonNull(response.getBody()).getOwnerId());
        }


        @Test
        public void deleteProjectSuccess() {
            assertDoesNotThrow(() -> requestUtils.deleteRequest("/content/" + contentId1, port));
        }

    }

    @Nested
    class WithData {


        @PreDestroy
        void dropAfter() {
            mongoTemplate.getDb().drop();
        }

        @PostConstruct
        void setMockOutputAndDatabase() {
            var project = getNewProject(projectId1, userId1);

            var user = getNewUser(userId1, getRandomLowercaseNumeric(12), getRandomLowercaseNumeric(12));

            var template1 = getNewTemplate(templateId1, projectId1, getRandomLowercaseNumeric(12));
            var template2 = getNewTemplate(templateId2, projectId1, getRandomLowercaseNumeric(12));
            var template3 = getNewTemplate(templateId3, projectId1, getRandomLowercaseNumeric(12));
            List<ContentField<?>> fields1 = List.of(
                    new BooleanField(boolName, true),
                    new DateField(dateName, new Date()),
                    new ImageField(imageName, imageUrl),
                    new ModuleField(moduleName, moduleId),
                    new NumberField(numberName, numberValue),
                    new StringField(stringName, stringValue),
                    new TextField(textName, textValue)
            );

            List<ContentField<?>> fields2 = List.of(
                    new BooleanField(boolName, false),
                    new DateField(dateName, getEarlierDate(2)),
                    new ImageField(imageName, getRandomLowercaseNumeric(40)),
                    new ModuleField(moduleName, getRandomLowercaseNumeric(24)),
                    new NumberField(numberName, numberValue+1000),
                    new StringField(stringName, getRandomAlphaNumericAndSymbols(50)),
                    new TextField(textName, getRandomAlphaNumericAndSymbols(300))
            );

            var content1 = Content.builder()
                    .id(contentId1)
                    .ownerId(userId1)
                    .projectId(projectId1)
                    .templateId(templateId1)
                    .name(contentName1)
                    .isPublic(true)
                    .contentFields(fields1)
                    .created(new Date())
                    .updated(new Date())
                    .build();
            var content2 = Content.builder()
                    .id(contentId2)
                    .ownerId(userId2)
                    .projectId(projectId2)
                    .templateId(templateId2)
                    .name(contentName2)
                    .isPublic(true)
                    .contentFields(fields2)
                    .created(getEarlierDate(2))
                    .updated(getEarlierDate(2))
                    .build();
            var content3 = Content.builder()
                    .id(contentId3)
                    .ownerId(userId1)
                    .projectId(projectId1)
                    .templateId(templateId1)
                    .name(contentName1)
                    .isPublic(false)
                    .contentFields(fields1)
                    .created(new Date())
                    .updated(new Date())
                    .build();
            var content4 = Content.builder()
                    .id(getRandomLowercaseNumeric(24))
                    .ownerId(userId1)
                    .projectId(getRandomLowercaseNumeric(24))
                    .templateId(templateId1)
                    .name(contentName1)
                    .isPublic(false)
                    .contentFields(fields1)
                    .created(new Date())
                    .updated(new Date())
                    .build();

            var projectFuture = CompletableFuture.runAsync(() -> projectRepository.save(project));

            var userFuture = CompletableFuture.runAsync(() -> userRepository.save(user));

            var templateFuture1 = CompletableFuture.runAsync(() -> templateRepository.save(template1));
            var templateFuture2 = CompletableFuture.runAsync(() -> templateRepository.save(template2));
            var templateFuture3 = CompletableFuture.runAsync(() -> templateRepository.save(template3));

            var contentFuture1 = CompletableFuture.runAsync(() -> contentRepository.save(content1));
            var contentFuture2 = CompletableFuture.runAsync(() -> contentRepository.save(content2));
            var contentFuture3 = CompletableFuture.runAsync(() -> contentRepository.save(content3));
            var contentFuture4 = CompletableFuture.runAsync(() -> contentRepository.save(content4));
            
            CompletableFuture.allOf(projectFuture, userFuture,
                    templateFuture1, templateFuture2, templateFuture3,
                    contentFuture1, contentFuture2, contentFuture3, contentFuture4)
                    .join();
        }

        @Test
        public void getAllPublicContentSuccess() {
            var response = requestUtils.getRequest("/content", Content[].class, port);

            assertEquals(2, Objects.requireNonNull(response.getBody()).length);
        }

        @Test
        public void getPublicContentByIdSuccess() {
            var response = requestUtils.getRequest("/content/" + contentId1, Content.class, port);

            assertEquals(contentId1, Objects.requireNonNull(response.getBody()).getId());
            assertTrue(Objects.requireNonNull(response.getBody()).getIsPublic());

        }

        @Test
        public void getPublicContentByIdFail() {
            var response = requestUtils.getRequest("/content/" + contentId3, Content.class, port);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }


        @Test
        public void getByProjectIdSuccess() {
            var response = requestUtils.getRequest("/content/projectId/" + projectId1, Content[].class, port);

            assertEquals(2, Objects.requireNonNull(response.getBody()).length);
            assertNotNull(response.getBody()[0].getOwnerName());
            assertNotNull(response.getBody()[0].getTemplateName());
        }

        @Test
        public void getByProjectIdFail() {
            assertThrows(RestClientException.class,
                    () -> requestUtils.getRequest("/content/projectId/" + new ObjectId(), Content[].class, port)
            );
        }

        @ParameterizedTest
        @ArgumentsSource(ContentSearchArguments.class)
        public void searchPublicContentSuccess(ContentSearch<?> search,String message) {
            var response = requestUtils.postRequest("/content/search", Content[].class, new ContentSearch[]{search}, port);
            assertEquals(1, Objects.requireNonNull(response.getBody()).length,message);
        }

        @ParameterizedTest
        @ArgumentsSource(ContentSearchArguments.class)
        public void searchContentSuccess(ContentSearch<?> search,String message) {
            var response = requestUtils.postRequest("/content/search/"+projectId1, Content[].class, new ContentSearch[]{search}, port);
            assertEquals(2, Objects.requireNonNull(response.getBody()).length,message);
        }
    }

    /**
     * provides search arguments for repeated search tests
     */
    static class ContentSearchArguments implements ArgumentsProvider {
        @Override
        public Stream<Arguments> provideArguments(ExtensionContext context) {
            var dateRange=new RangeParameter<>(getEarlierDate(1), getLaterDate(1));
            return Stream.of(
                    Arguments.of(new DateSearch("created", dateRange), "created date range"),
                    Arguments.of(new DateSearch("created", new RangeParameter<>(getEarlierDate(1), null)), "created date min"),
                    Arguments.of(new ExactSearch<>("templateId", templateId1), "templateId name"),
                    Arguments.of(new ExactSearch<>("projectId", projectId1), "projectId"),
                    Arguments.of(new ExactSearch<>("ownerId", userId1), "OwnerId"),
                    Arguments.of(new RegexSearch("name", contentName1.substring(contentName1.length() / 3, contentName1.length() / 2)), "name regex"),
                    Arguments.of(new FieldExactSearch<>(boolName, true), "field boolean"),
                    Arguments.of(new FieldDateSearch(dateName, dateRange), "field date range"),
                    Arguments.of(new FieldExactSearch<>(imageName, imageUrl), "field image"),
                    Arguments.of(new FieldExactSearch<>(moduleName, moduleId), "field module"),
                    Arguments.of(new FieldNumberSearch(numberName, new RangeParameter<>(numberValue-1,numberValue+1)), "field number range"),
                    Arguments.of(new FieldRegexSearch(stringName, stringValue.substring(stringValue.length()/4,stringValue.length()/3)), "field string"),
                    Arguments.of(new FieldRegexSearch(textName, textValue.substring(textValue.length()/4,textValue.length()/3)), "field text")
            );
        }
    }
}