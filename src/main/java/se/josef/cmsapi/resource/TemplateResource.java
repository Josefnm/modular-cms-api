package se.josef.cmsapi.resource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import se.josef.cmsapi.adapter.TemplateAdapter;
import se.josef.cmsapi.model.document.Template;
import se.josef.cmsapi.model.web.TemplateForm;
import se.josef.cmsapi.service.TemplateService;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RequestMapping(value = "template", produces = APPLICATION_JSON_VALUE)
@RestController
public class TemplateResource {

    private final TemplateService templateService;
    private final TemplateAdapter templateAdapter;

    public TemplateResource(TemplateService templateService, TemplateAdapter templateAdapter) {
        this.templateService = templateService;
        this.templateAdapter = templateAdapter;
    }

    @PostMapping()
    public Template saveTemplate(@RequestBody TemplateForm templateForm) {
        return templateService.saveTemplate(templateForm);
    }

    @GetMapping("/projectId/{projectId}")
    public List<Template> getByProjectId(@PathVariable String projectId) {
        return templateAdapter.findByProjectId(projectId);
    }

    @GetMapping("/{id}")
    public Template getTemplateById(@PathVariable String id) {
        return templateService.getTemplateById(id);
    }

    @GetMapping("/search/{projectId}")
    public List<Template> searchByNameAndProjectId(@PathVariable String projectId, @RequestParam String searchString) {
        System.out.println(projectId);
        return templateAdapter.searchByNameAndProjectId(searchString, projectId);
    }

}
