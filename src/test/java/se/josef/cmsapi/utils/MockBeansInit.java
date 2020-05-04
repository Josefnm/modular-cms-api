package se.josef.cmsapi.utils;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Answers;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import se.josef.cmsapi.repository.ContentRepository;
import se.josef.cmsapi.repository.ProjectRepository;
import se.josef.cmsapi.repository.TemplateRepository;
import se.josef.cmsapi.repository.UserRepository;
import se.josef.cmsapi.service.UserService;

import javax.annotation.PostConstruct;


/**
 * Load all mockbeans required by context or common methods.
 */
@Component
public class MockBeansInit {
    @MockBean
    private MongoTemplate mongoTemplate;

    @MockBean
    private ContentRepository contentRepository;
    @MockBean
    private TemplateRepository templateRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private ProjectRepository projectRepository;

    @SpyBean
    private UserService userService;

    @PostConstruct
    void setMockOutput() {

        Mockito.doReturn("userId").when(userService).getUserId();

    }
}
