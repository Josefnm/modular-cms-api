package se.josef.cmsapi;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import se.josef.cmsapi.enums.DataType;
import se.josef.cmsapi.model.document.Template;
import se.josef.cmsapi.model.document.TemplateField;
import se.josef.cmsapi.repository.TemplateRepository;
import se.josef.cmsapi.service.UserService;
import se.josef.cmsapi.utils.RequestUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
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
    private static final Template template = getNewTemplate();

    @LocalServerPort
    private int port;
    @Autowired
    private TemplateRepository templateRepository;
    @Autowired
    private UserService userService;

    @Autowired
    private RequestUtils requestUtils;

    @BeforeEach
    void setMockOutput() {
        System.out.println("before");
        Template template = getNewTemplate();
        doReturn(userId).when(userService).getUserId();
        when(templateRepository.save(any())).thenReturn(template);
        when(templateRepository.findByIdAndOwnerId(templateId, userId)).thenReturn(java.util.Optional.of(template));
    }


    @Test
    public void saveTemplateSuccess() {
        ResponseEntity<Template> response = requestUtils.postRequest("/template", Template.class, getNewTemplate(), port);
        assertEquals(templateId, response.getBody().getId());
    }

    @Test
    public void getTemplateByIdSuccess() {
        ResponseEntity<Template> response = requestUtils.getRequest("/template/" + templateId, Template.class, port);
        assertEquals(templateId, response.getBody().getId());
    }

    @Test
    public void getTemplateByIdFail() {
        ResponseEntity<Template> response = requestUtils.getRequest("/template/" + getRandomLowercaseNumeric(24), Template.class, port);
        assertNotEquals(templateId, response.getBody().getId());
    }


    private static Template getNewTemplate() {
        List<TemplateField> tfs = new ArrayList<>();
        tfs.add(new TemplateField(getRandomAlphabets(15), DataType.STRING));
        tfs.add(new TemplateField(getRandomAlphabets(15), DataType.IMAGE));

        return new Template(templateId,
                userId,
                getRandomAlphabets(15),
                new Date(),
                new Date(),
                getRandomAlphabets(15),
                getRandomAlphabets(100),
                tfs);
    }
}
