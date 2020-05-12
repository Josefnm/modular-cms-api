package se.josef.cmsapi.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import se.josef.cmsapi.exception.TemplateException;
import se.josef.cmsapi.model.document.Template;
import se.josef.cmsapi.model.web.TemplateForm;
import se.josef.cmsapi.repository.TemplateRepository;

import java.util.List;

@Service
@Slf4j
public class TemplateService {

    private final TemplateRepository templateRepository;

    public TemplateService(TemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }

    /**
     * save new template with data from template form
     * @param templateForm
     * @return
     */
    public Template saveTemplate(TemplateForm templateForm) {
        Template template = Template.builder()
                .templateFields(templateForm.getTemplateFields())
                .name(templateForm.getName())
                .projectId(templateForm.getProjectId())
                .description(templateForm.getDescription())
                .build();
        return templateRepository.save(template);
    }

    /**
     * find templates that belongs to project
     */
    public List<Template> findByProjectId(String projectId) {
        var x=templateRepository.findByProjectIdOrderByCreatedDesc(projectId);
        log.info("size "+x.size());
        return templateRepository.findByProjectIdOrderByCreatedDesc(projectId);
    }

    /**
     * Finds a template by its id
     */
    public Template getTemplateById(String id) {
        return templateRepository
                .findById(id)
                .orElseThrow(() ->
                        new TemplateException(String.format("Content with id %s is unavailable", id))
                );
    }

    /**
     * Asynchronous deletion of all the templates that belong to project
     */
    @Async
    public void deleteByProjectId(String projectId) {
        templateRepository.deleteByProjectId(projectId);
    }

    /**
     * Find template by project and regex match for name
     */
    public List<Template> searchByNameAndProjectId(String name, String projectId) {
        return templateRepository.findByNameRegexAndProjectIdOrderByCreated(name, projectId);
    }
}
