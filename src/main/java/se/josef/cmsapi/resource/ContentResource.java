package se.josef.cmsapi.resource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.josef.cmsapi.adapter.ContentAdapter;
import se.josef.cmsapi.model.document.Content;
import se.josef.cmsapi.model.web.ContentForm;
import se.josef.cmsapi.model.web.contentsearch.ContentSearch;
import se.josef.cmsapi.service.ContentService;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RequestMapping(value = "content", produces = APPLICATION_JSON_VALUE)
@RestController
public class ContentResource {

    private final ContentService contentService;
    private final ContentAdapter contentAdapter;

    public ContentResource(ContentService contentService, ContentAdapter contentAdapter) {
        this.contentService = contentService;
        this.contentAdapter = contentAdapter;
    }

    @PostMapping()
    public Content addContent(@RequestBody ContentForm contentForm) {
        return contentService.saveContent(contentForm);
    }

    @PostMapping("/update")
    public Content updateContent(@RequestBody ContentForm contentForm) {
        return contentAdapter.updateContent(contentForm);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContent(@PathVariable String id) {
        contentAdapter.deleteContent(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping()
    public List<Content> getAllPublicContent() {
        return contentService.getAllPublicContent();
    }

    @GetMapping("/{id}")
    public Content getPublicContentById(@PathVariable String id) {
        return contentService.getContentByIdAndPublic(id);
    }

    @GetMapping("/projectId/{projectId}")
    public List<Content> getByProjectId(@PathVariable String projectId) {
        return contentAdapter.findByProjectId(projectId);
    }

    @PostMapping("/searchPublic")
    public List<Content> searchPublicContent(@RequestBody List<ContentSearch<?>> searchFields) {
        return contentService.searchPublicContent(searchFields);
    }

    @PostMapping("/search/{projectId}")
    public List<Content> searchContent(@RequestBody List<ContentSearch<?>> searchFields, @PathVariable String projectId) {
        return contentAdapter.searchContent(searchFields, projectId);
    }

}
