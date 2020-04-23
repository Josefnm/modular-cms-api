package se.josef.cmsapi.service;

import org.springframework.stereotype.Service;
import se.josef.cmsapi.model.document.Template;
import se.josef.cmsapi.repository.TemplateRepository;

import java.util.List;

@Service
public class TemplateService {

    private final TemplateRepository templateRepository;

    public TemplateService(TemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }

    public Template saveTemplate(Template template) {
        return templateRepository.save(template);
    }

    public List<Template> getAllTemplates() {
        return templateRepository.findAll();
    }
}
