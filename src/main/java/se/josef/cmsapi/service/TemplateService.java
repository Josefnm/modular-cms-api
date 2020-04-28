package se.josef.cmsapi.service;

import org.springframework.stereotype.Service;
import se.josef.cmsapi.exception.ContentException;
import se.josef.cmsapi.model.document.Template;
import se.josef.cmsapi.repository.TemplateRepository;

import java.util.List;
import java.util.Optional;

@Service
public class TemplateService {

    private final TemplateRepository templateRepository;
    private final UserService userService;


    public TemplateService(TemplateRepository templateRepository, UserService userService) {
        this.templateRepository = templateRepository;
        this.userService = userService;
    }

    public Template saveTemplate(Template template) {
        return templateRepository.save(template);
    }

    public List<Template> getAllTemplates() {
        return templateRepository.findAll();
    }


    public Template getContentById(String id) {
        return templateRepository
                .findByIdAndOwnerIdOrIsPublic(id,  userService.getUserId(),true)
                .orElseThrow(() -> new ContentException(String.format("Content with id %s is unavailable", id)));
    }


    public List<Template> getContentForCurrentUser() {
        return templateRepository
                .findByOwnerIdOrderByCreatedDesc(userService.getUserId());
    }
}
