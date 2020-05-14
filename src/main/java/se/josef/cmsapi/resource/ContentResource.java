package se.josef.cmsapi.resource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.josef.cmsapi.adapter.ContentAdapter;
import se.josef.cmsapi.model.document.Content;
import se.josef.cmsapi.model.web.ContentForm;
import se.josef.cmsapi.model.web.ContentUpdateForm;
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

    /**
     * Saves a new content document
     *
     * @param contentForm for creating new content
     * @return the saved content
     */
    @PostMapping()
    public Content addContent(@RequestBody ContentForm contentForm) {
        return contentService.saveContent(contentForm);
    }

    /**
     * Updates contents name, isPublic and/or contentfields
     *
     * @param contentUpdateForm update data
     * @return updated content
     */
    @PostMapping("/update")
    public Content updateContent(@RequestBody ContentUpdateForm contentUpdateForm) {
        return contentAdapter.updateContent(contentUpdateForm);
    }

    /**
     * delete the content
     *
     * @param id of the content
     * @return 200 if success
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContent(@PathVariable String id) {
        contentAdapter.deleteContent(id);
        return ResponseEntity.ok().build();
    }

    /**
     * get all public content
     *
     * @return found content
     */
    @GetMapping()
    public List<Content> getAllPublicContent() {
        return contentService.getAllPublicContent();
    }

    /**
     * get a content document if it is public
     *
     * @param id of the content
     * @return found content
     */
    @GetMapping("/{id}")
    public Content getPublicContentById(@PathVariable String id) {
        return contentService.getContentByIdAndPublic(id);
    }

    /**
     * get content that belongs to a project
     *
     * @param projectId of the project
     * @return found content
     */
    @GetMapping("/projectId/{projectId}")
    public List<Content> getByProjectId(@PathVariable String projectId) {
        return contentAdapter.findByProjectId(projectId);
    }

    /**
     * searches for content that is public and matches the criteria specified
     *
     * @param contentSearches criteria for the search
     * @return matching contents
     */
    @PostMapping("/search")
    public List<Content> searchPublicContent(@RequestBody List<ContentSearch<?>> contentSearches) {
        return contentService.searchPublicContent(contentSearches);
    }

    /**
     * searches for content that belnogs to project and matches the criteria specified
     *
     * @param searchFields criteria for the search
     * @param projectId    of project they belong to
     * @return matching content
     */
    @PostMapping("/search/{projectId}")
    public List<Content> searchContent(@RequestBody List<ContentSearch<?>> searchFields,
                                       @PathVariable String projectId) {
        return contentAdapter.searchContent(searchFields, projectId);
    }

}
