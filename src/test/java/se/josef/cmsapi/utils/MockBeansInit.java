package se.josef.cmsapi.utils;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import se.josef.cmsapi.repository.ContentRepository;
import se.josef.cmsapi.repository.ProjectRepository;
import se.josef.cmsapi.repository.TemplateRepository;
import se.josef.cmsapi.repository.UserRepository;
import se.josef.cmsapi.service.UserService;

/**
 * Load mocks of all beans required by context or common methods.
 * These can be accessed by autowired in test classes
 */
@Component
public class MockBeansInit {

}
