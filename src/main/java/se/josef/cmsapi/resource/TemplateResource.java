package se.josef.cmsapi.resource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import se.josef.cmsapi.model.document.Template;
import se.josef.cmsapi.service.TemplateService;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@Slf4j
@RequestMapping(value = "/templates", produces = APPLICATION_JSON_VALUE)
@RestController
public class TemplateResource {

    private final TemplateService templateService;

    public TemplateResource(TemplateService templateService) {
        this.templateService = templateService;
    }

    @PostMapping()
    public @ResponseBody
    Template addUser(@RequestBody Template template) {
        return templateService.saveTemplate(template);
    }

    @GetMapping()
    public @ResponseBody
    List<Template> getAllComments() {
        return templateService.getAllTemplates();
    }
}
