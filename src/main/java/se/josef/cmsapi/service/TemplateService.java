package se.josef.cmsapi.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import se.josef.cmsapi.exception.TemplateException;
import se.josef.cmsapi.model.document.Template;
import se.josef.cmsapi.model.web.TemplateForm;
import se.josef.cmsapi.repository.ProjectRepository;
import se.josef.cmsapi.repository.TemplateRepository;
import se.josef.cmsapi.util.UserUtils;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class TemplateService {

    private final TemplateRepository templateRepository;
    private final ProjectRepository projectRepository;
    private final UserUtils userUtils;


    public TemplateService(TemplateRepository templateRepository, ProjectRepository projectRepository, UserUtils userUtils) {
        this.templateRepository = templateRepository;
        this.projectRepository = projectRepository;
        this.userUtils = userUtils;
    }

    public Template saveTemplate(TemplateForm templateForm) {
        var created = new Date();
        var ownerId = userUtils.getUserId();
        Template template = Template.builder()
                .ownerId(ownerId)
                .templateFields(templateForm.getTemplateFields())
                .name(templateForm.getName())
                .projectId(templateForm.getProjectId())
                .description(templateForm.getDescription())
                .created(created)
                .updated(created)
                .build();

        return templateRepository.save(template);
    }

    public List<Template> findByProjectId(String projectId) {
        var userId = userUtils.getUserId();
        projectRepository
                .findByMemberIdsAndId(userId, projectId)
                .orElseThrow(() ->
                        new TemplateException(String.format("User doesn't have access to project with id: %s", projectId))
                );
        return templateRepository.findByProjectIdOrderByCreatedDesc(projectId);
    }

    public Template getTemplateById(String id) {
        return templateRepository
                .findById(id)
                .orElseThrow(() ->
                        new TemplateException(String.format("Content with id %s is unavailable", id))
                );
    }
}
