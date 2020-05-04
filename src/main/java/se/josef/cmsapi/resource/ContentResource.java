package se.josef.cmsapi.resource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import se.josef.cmsapi.model.document.Content;
import se.josef.cmsapi.model.web.ContentForm;
import se.josef.cmsapi.service.ContentService;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@Slf4j
@RequestMapping(value = "/content", produces = APPLICATION_JSON_VALUE)
@RestController
public class ContentResource {

    private final ContentService contentService;

    public ContentResource(ContentService contentService) {
        this.contentService = contentService;
    }

    @PostMapping()
    public @ResponseBody
    Content addContent(@RequestBody ContentForm contentForm) {
        return contentService.saveContent(contentForm);
    }

    @GetMapping()
    public @ResponseBody
    List<Content> getAllPublicContent() {
        return contentService.getAllPublicContent();
    }

    @GetMapping("/{id}")
    public @ResponseBody
    Content getContentById(@PathVariable String id) {
        return contentService.getContentById(id);
    }

    @GetMapping("/user")
    public @ResponseBody
    List<Content> getContentForCurrentUser() {
        return contentService.getContentForCurrentUser();
    }

    @GetMapping("/projectId/{projectId}")
    public @ResponseBody
    List<Content> getByProjectId(@PathVariable String projectId) {
        return contentService.findByProjectId(projectId);
    }

}
