package se.josef.cmsapi.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import se.josef.cmsapi.exception.ContentException;
import se.josef.cmsapi.model.document.Template;
import se.josef.cmsapi.model.web.TemplateForm;
import se.josef.cmsapi.repository.TemplateRepository;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class TemplateService {

    private final TemplateRepository templateRepository;
    private final UserService userService;


    public TemplateService(TemplateRepository templateRepository, UserService userService) {
        this.templateRepository = templateRepository;
        this.userService = userService;
    }

    public Template saveTemplate(TemplateForm templateForm) {
        Date created=new Date();
        String ownerId=userService.getUserId();
        Template template=Template.builder()
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
        return templateRepository.findByProjectIdOrderByCreatedDesc(projectId);
    }

    //TODO check access against project owner/members instead
    public Template getTemplateById(String id) {
        return templateRepository
                .findByIdAndOwnerId(id, userService.getUserId())
                .orElseThrow(() ->
                        new ContentException(String.format("Content with id %s is unavailable", id))
                );
    }

    public List<Template> getTemplateForCurrentUser() {
        return templateRepository
                .findByOwnerIdOrderByCreatedDesc(userService.getUserId());
    }
}
