package se.josef.cmsapi;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import se.josef.cmsapi.enums.DataType;
import se.josef.cmsapi.model.document.Template;
import se.josef.cmsapi.model.document.TemplateField;
import se.josef.cmsapi.repository.TemplateRepository;
import se.josef.cmsapi.service.UserService;
import se.josef.cmsapi.utils.RequestUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static se.josef.cmsapi.utils.MockDataUtil.getRandomAlphabets;
import static se.josef.cmsapi.utils.MockDataUtil.getRandomLowercaseNumeric;

@Slf4j
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration(exclude = {MongoAutoConfiguration.class, SecurityAutoConfiguration.class})
public class TemplateTests {

    private static final String templateId = getRandomLowercaseNumeric(24);
    private static final String userId = getRandomLowercaseNumeric(24);

    @LocalServerPort
    private int port;
    @Autowired
    private TemplateRepository templateRepository;

    @Autowired
    private RequestUtils requestUtils;

    @BeforeEach
    void setMockOutput() {
        Template template = getNewTemplate();
        when(templateRepository.save(any())).thenReturn(template);
    }


    @Test
    public void saveTemplate() {
        ResponseEntity<Template> response = requestUtils.httpRequest("/templates", HttpMethod.POST, Template.class, getNewTemplate(), port);
        assertEquals(templateId, response.getBody().getId());

    }


    private Template getNewTemplate() {
        List<TemplateField> tfs = new ArrayList<>();
        tfs.add(new TemplateField(getRandomAlphabets(15), DataType.STRING));
        tfs.add(new TemplateField(getRandomAlphabets(15), DataType.IMAGE));

        return new Template(templateId,
                userId,
                new Date(),
                new Date(),
                getRandomAlphabets(15),
                getRandomAlphabets(100),
                true,
                tfs);
    }
}
