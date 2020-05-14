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

    /**
     * Saves a new template
     *
     * @param templateForm for creating new template
     * @return the saved template
     */
    @PostMapping()
    public Template saveTemplate(@RequestBody TemplateForm templateForm) {
        return templateService.saveTemplate(templateForm);
    }

    /**
     * get all templates that belong to project
     *
     * @param projectId the project the templates belong to
     * @return found templates
     */
    @GetMapping("/projectId/{projectId}")
    public List<Template> getTemplateByProjectId(@PathVariable String projectId) {
        return templateAdapter.findByProjectId(projectId);
    }

    /**
     * get a template by its id
     *
     * @param id of the template
     * @return found template
     */
    @GetMapping("/{id}")
    public Template getTemplateById(@PathVariable String id) {
        return templateService.getTemplateById(id);
    }

    /**
     * regex search on template name for templates that belong to a specific project.
     *
     * @param projectId    for the project
     * @param searchString regex for partial match
     * @return templates matching criteria
     */
    @GetMapping("/search/{projectId}")
    public List<Template> searchTemplateByNameAndProjectId(@PathVariable String projectId, @RequestParam String searchString) {
        return templateAdapter.searchByNameAndProjectId(searchString, projectId);
    }

}
