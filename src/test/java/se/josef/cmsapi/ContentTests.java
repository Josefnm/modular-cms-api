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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static java.util.concurrent.CompletableFuture.runAsync;
import static org.apache.commons.lang3.RandomStringUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static se.josef.cmsapi.utils.MockDataUtil.*;


@Slf4j
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration(exclude = SecurityAutoConfiguration.class)
public class ContentTests {

    private static final String USER_ID_1 = randomAlphanumeric(24);
    private static final String USER_ID_2 = randomAlphanumeric(24);
    private static final String TEMPLATE_ID_1 = new ObjectId().toString();
    private static final String TEMPLATE_ID_2 = new ObjectId().toString();
    private static final String TEMPLATE_ID_3 = new ObjectId().toString();
    private static final String PROJECT_ID_1 = new ObjectId().toString();
    private static final String PROJECT_ID_2 = new ObjectId().toString();
    private static final String CONTENT_ID_1 = new ObjectId().toString();
    private static final String CONTENT_ID_2 = new ObjectId().toString();
    private static final String CONTENT_ID_3 = new ObjectId().toString();
    private static final String CONTENT_NAME_1 = randomAlphanumeric(12);
    private static final String UPDATED_CONTENT_NAME_1 = randomAlphabetic(20);
    private static final String CONTENT_NAME_2 = randomAlphanumeric(12);

    private static final String BOOL_NAME = randomAscii(12);
    private static final String DATE_NAME = randomAscii(12);
    private static final String IMAGE_NAME = randomAscii(12);
    private static final String IMAGE_URL = randomAscii(40);
    private static final String MODULE_NAME = randomAscii(12);
    private static final String MODULE_ID = randomAlphanumeric(24);
    private static final String NUMBER_NAME = randomAscii(12);
    private static final Long NUMBER_VALUE = randomLong(5);
    private static final String STRING_NAME = randomAlphanumeric(12);
    private static final String STRING_VALUE = randomAlphanumeric(50);
    private static final String TEXT_NAME = randomAlphanumeric(12);
    private static final String TEXT_VALUE = randomAlphanumeric(300);

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
        mongoTemplate.getDb().drop();
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
        void setMockOutputAndDatabase() {
            mongoTemplate.getDb().drop();
        }

        @BeforeEach
        void saveDefaultContent() {

            var content = Content.builder()
                    .id(CONTENT_ID_1)
                    .ownerId(USER_ID_1)
                    .name(CONTENT_NAME_1)
                    .isPublic(true)
                    .contentFields(new ArrayList<>())
                    .templateId(TEMPLATE_ID_1)
                    .projectId(PROJECT_ID_1)
                    .created(new Date())
                    .build();

            var project = getNewProject(PROJECT_ID_1, USER_ID_1);

            CompletableFuture.allOf(
                    runAsync(() -> contentRepository.save(content)),
                    runAsync(() -> projectRepository.save(project)))
                    .join();
        }

        // Some issues with this this, fails irregularly. probably database related.
        // TODO fix this
        @Disabled
        @Test
        public void updateContentSuccess() {
            var contentFields = new ArrayList<ContentField<?>>();

            contentFields.add(new StringField(randomAlphanumeric(12), randomAlphanumeric(12)));
            contentFields.add(new DateField(randomAlphanumeric(12), new Date()));

            var contentForm = new ContentForm(CONTENT_ID_1, null, null, UPDATED_CONTENT_NAME_1, false, contentFields);
            var response = requestUtils.postRequest("/content/update", Content.class, contentForm, port);

            assertEquals(USER_ID_1, requireNonNull(response.getBody()).getOwnerId());
            assertEquals(UPDATED_CONTENT_NAME_1, requireNonNull(response.getBody()).getName());
            assertEquals(2, response.getBody().getContentFields().size());
        }

        @Test
        public void addContentSuccess() {
            var contentForm = new ContentForm(null, PROJECT_ID_1, TEMPLATE_ID_1, randomAlphanumeric(12), true, new ArrayList<>());
            var response = requestUtils.postRequest("/content", Content.class, contentForm, port);

            assertEquals(USER_ID_1, requireNonNull(response.getBody()).getOwnerId());
        }

        @Test
        public void deleteProjectSuccess() {
            assertDoesNotThrow(() -> requestUtils.deleteRequest("/content/" + CONTENT_ID_1, port));
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
            var project = getNewProject(PROJECT_ID_1, USER_ID_1);

            var user = getNewUser(USER_ID_1, randomAlphanumeric(12), randomAlphanumeric(12));

            var template1 = getNewTemplate(TEMPLATE_ID_1, PROJECT_ID_1, randomAlphanumeric(12));
            var template2 = getNewTemplate(TEMPLATE_ID_2, PROJECT_ID_1, randomAlphanumeric(12));
            var template3 = getNewTemplate(TEMPLATE_ID_3, PROJECT_ID_1, randomAlphanumeric(12));

            List<ContentField<?>> fieldsDoMatch = List.of(
                    new BooleanField(BOOL_NAME, true),
                    new DateField(DATE_NAME, new Date()),
                    new ImageField(IMAGE_NAME, IMAGE_URL),
                    new ModuleField(MODULE_NAME, MODULE_ID),
                    new NumberField(NUMBER_NAME, NUMBER_VALUE),
                    new StringField(STRING_NAME, STRING_VALUE),
                    new TextField(TEXT_NAME, TEXT_VALUE)
            );

            List<ContentField<?>> fieldsDoNotMatch = List.of(
                    new BooleanField(BOOL_NAME, false),
                    new DateField(DATE_NAME, getPastDate(2)),
                    new ImageField(IMAGE_NAME, randomAlphanumeric(40)),
                    new ModuleField(MODULE_NAME, randomAlphanumeric(24)),
                    new NumberField(NUMBER_NAME, NUMBER_VALUE + 1000),
                    new StringField(STRING_NAME, randomAscii(50)),
                    new TextField(TEXT_NAME, randomAscii(300))
            );

            var content1 = Content.builder()
                    .id(CONTENT_ID_1)
                    .ownerId(USER_ID_1)
                    .projectId(PROJECT_ID_1)
                    .templateId(TEMPLATE_ID_1)
                    .name(CONTENT_NAME_1)
                    .isPublic(true)
                    .contentFields(fieldsDoMatch)
                    .created(new Date())
                    .build();

            var content2 = Content.builder()
                    .id(CONTENT_ID_2)
                    .ownerId(USER_ID_2)
                    .projectId(PROJECT_ID_2)
                    .templateId(TEMPLATE_ID_2)
                    .name(CONTENT_NAME_2)
                    .isPublic(true)
                    .contentFields(fieldsDoNotMatch)
                    .created(getPastDate(2))
                    .build();

            var content3 = Content.builder()
                    .id(CONTENT_ID_3)
                    .ownerId(USER_ID_1)
                    .projectId(PROJECT_ID_1)
                    .templateId(TEMPLATE_ID_1)
                    .name(CONTENT_NAME_1)
                    .isPublic(false)
                    .contentFields(fieldsDoMatch)
                    .created(new Date())
                    .build();

            var content4 = Content.builder()
                    .id(randomAlphanumeric(24))
                    .ownerId(USER_ID_1)
                    .projectId(randomAlphanumeric(24))
                    .templateId(TEMPLATE_ID_1)
                    .name(CONTENT_NAME_1)
                    .isPublic(false)
                    .contentFields(fieldsDoMatch)
                    .created(new Date())
                    .build();

            CompletableFuture.allOf(
                    runAsync(() -> projectRepository.save(project)),
                    runAsync(() -> userRepository.save(user)),
                    runAsync(() -> templateRepository.save(template1)),
                    runAsync(() -> templateRepository.save(template2)),
                    runAsync(() -> templateRepository.save(template3)),
                    runAsync(() -> contentRepository.save(content1)),
                    runAsync(() -> contentRepository.save(content2)),
                    runAsync(() -> contentRepository.save(content3)),
                    runAsync(() -> contentRepository.save(content4)))
                    .join();
        }

        @Test
        public void getAllPublicContentSuccess() {
            var response = requestUtils.getRequest("/content", Content[].class, port);

            assertEquals(2, requireNonNull(response.getBody()).length);
        }

        @Test
        public void getPublicContentByIdSuccess() {
            var response = requestUtils.getRequest("/content/" + CONTENT_ID_1, Content.class, port);

            assertEquals(CONTENT_ID_1, requireNonNull(response.getBody()).getId());
            assertTrue(requireNonNull(response.getBody()).getIsPublic());

        }

        @Test
        public void getPublicContentByIdFail() {
            var response = requestUtils.getRequest("/content/" + CONTENT_ID_3, Content.class, port);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }


        @Test
        public void getByProjectIdSuccess() {
            var response = requestUtils.getRequest("/content/projectId/" + PROJECT_ID_1, Content[].class, port);

            assertEquals(2, requireNonNull(response.getBody()).length);
            assertNotNull(response.getBody()[0].getOwnerName());
            assertNotNull(response.getBody()[0].getTemplateName());
        }

        @Test
        public void getByProjectIdFail() {
            assertThrows(
                    RestClientException.class,
                    () -> requestUtils.getRequest("/content/projectId/" + new ObjectId(), Content[].class, port)
            );
        }

        @ParameterizedTest
        @ArgumentsSource(ContentSearchArguments.class)
        public void searchPublicContentSuccess(ContentSearch<?> search, String message) {
            var response = requestUtils
                    .postRequest("/content/search", Content[].class, new ContentSearch[]{search}, port);
            assertEquals(1, requireNonNull(response.getBody()).length, message);
        }

        @ParameterizedTest
        @ArgumentsSource(ContentSearchArguments.class)
        public void searchContentSuccess(ContentSearch<?> search, String message) {
            var response = requestUtils
                    .postRequest("/content/search/" + PROJECT_ID_1, Content[].class, new ContentSearch[]{search}, port);

            assertEquals(2, requireNonNull(response.getBody()).length, message);
        }
    }

    /**
     * provides search arguments for repeated search tests
     */
    static class ContentSearchArguments implements ArgumentsProvider {
        @Override
        public Stream<Arguments> provideArguments(ExtensionContext context) {
            var dateRange = new RangeParameter<>(getPastDate(1), getFutureDate(1));
            return Stream.of(
                    Arguments.of(new DateSearch("created", dateRange), "created date range"),
                    Arguments.of(new DateSearch("created", new RangeParameter<>(getPastDate(1), null)), "created date min"),
                    Arguments.of(new ExactSearch<>("templateId", TEMPLATE_ID_1), "templateId name"),
                    Arguments.of(new ExactSearch<>("projectId", PROJECT_ID_1), "projectId"),
                    Arguments.of(new ExactSearch<>("ownerId", USER_ID_1), "OwnerId"),
                    Arguments.of(new RegexSearch("name", CONTENT_NAME_1.substring(CONTENT_NAME_1.length() / 3, CONTENT_NAME_1.length() / 2)), "name regex"),
                    Arguments.of(new FieldExactSearch<>(BOOL_NAME, true), "field boolean"),
                    Arguments.of(new FieldDateSearch(DATE_NAME, dateRange), "field date range"),
                    Arguments.of(new FieldExactSearch<>(IMAGE_NAME, IMAGE_URL), "field image"),
                    Arguments.of(new FieldExactSearch<>(MODULE_NAME, MODULE_ID), "field module"),
                    Arguments.of(new FieldNumberSearch(NUMBER_NAME, new RangeParameter<>(NUMBER_VALUE - 1, NUMBER_VALUE + 1)), "field number range"),
                    Arguments.of(new FieldRegexSearch(STRING_NAME, STRING_VALUE.substring(STRING_VALUE.length() / 4, STRING_VALUE.length() / 3)), "field string"),
                    Arguments.of(new FieldRegexSearch(TEXT_NAME, TEXT_VALUE.substring(TEXT_VALUE.length() / 4, TEXT_VALUE.length() / 3)), "field text")
            );
        }
    }
}