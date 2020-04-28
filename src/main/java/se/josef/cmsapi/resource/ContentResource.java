package se.josef.cmsapi.resource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import se.josef.cmsapi.model.document.Content;
import se.josef.cmsapi.service.ContentService;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@Slf4j
@RequestMapping(value = "/api/content", produces = APPLICATION_JSON_VALUE)
@RestController
public class ContentResource {

    private final ContentService contentService;

    public ContentResource(ContentService contentService) {
        this.contentService = contentService;
    }

    @PostMapping()
    public @ResponseBody
    Content addUser(@RequestBody Content content) {
        return contentService.saveContent(content);
    }

    @GetMapping()
    public @ResponseBody
    List<Content> getAllContent() {
        return contentService.getAllContent();
    }

    @GetMapping("/getById/{id}")
    public @ResponseBody
    Content getContentById(@PathVariable String id) {
        return contentService.getContentById(id);
    }

    @GetMapping("/getOwn")
    public @ResponseBody
    List<Content> getContentForCurrentUser() {
        return contentService.getContentForCurrentUser();
    }

}
