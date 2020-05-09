package se.josef.cmsapi;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClientException;
import se.josef.cmsapi.enums.DataType;
import se.josef.cmsapi.model.document.Project;
import se.josef.cmsapi.model.document.Template;
import se.josef.cmsapi.model.document.TemplateField;
import se.josef.cmsapi.repository.ProjectRepository;
import se.josef.cmsapi.repository.TemplateRepository;
import se.josef.cmsapi.util.UserUtils;
import se.josef.cmsapi.utils.RequestUtils;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static se.josef.cmsapi.utils.MockDataUtil.getRandomAlphabets;
import static se.josef.cmsapi.utils.MockDataUtil.getRandomLowercaseNumeric;

@Slf4j
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration
public class TemplateTests {

    private static final String templateId = getRandomLowercaseNumeric(24);
    private static final String userId = getRandomLowercaseNumeric(24);
    private static final String projectId = getRandomLowercaseNumeric(24);

    @LocalServerPort
    private int port;
    @Autowired
    private TemplateRepository templateRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @MockBean
    private UserUtils userUtils;

    @Autowired
    private RequestUtils requestUtils;
/*
    @BeforeEach
    void setMockOutput() {
        log.info(System.getenv().toString());
        var template = getNewTemplate();
        doReturn(userId).when(userUtils).getUserId();
        when(templateRepository.save(any())).thenReturn(template);
        when(projectRepository.findByMemberIdsAndId(userId, projectId)).thenReturn(Optional.of(new Project()));
        when(templateRepository.findByProjectIdOrderByCreatedDesc(projectId)).thenReturn(Collections.singletonList(template));
    }

    @Test
    public void saveTemplateSuccess() {
        var response = requestUtils.postRequest("/template", Template.class, getNewTemplate(), port);
        System.out.println(response.getBody());
        assertEquals(templateId, Objects.requireNonNull(response.getBody()).getId());
    }

    @Test
    public void getTemplatesByProjectIdSuccess() {
        var response = requestUtils.getRequest("/template/projectId/" + projectId, Template[].class, port);
        assertEquals(templateId, Objects.requireNonNull(response.getBody())[0].getId());

    }

    @Test
    public void getTemplatesByProjectIdFail() {
        Assertions.assertThrows(RestClientException.class,
                () -> requestUtils.getRequest("/template/projectId/" + getRandomLowercaseNumeric(24),
                        Template[].class,
                        port)
        );
    }


    private static Template getNewTemplate() {
        List<TemplateField> tfs = new ArrayList<>();
        tfs.add(new TemplateField(getRandomAlphabets(15), DataType.STRING.getType().getSimpleName()));
        tfs.add(new TemplateField(getRandomAlphabets(15), DataType.IMAGE.getType().getSimpleName()));

        return new Template(templateId,
                userId,
                getRandomAlphabets(15),
                new Date(),
                new Date(),
                getRandomAlphabets(15),
                getRandomAlphabets(100),
                true,
                tfs);
    }*/
}
