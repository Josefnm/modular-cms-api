package se.josef.cmsapi.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import se.josef.cmsapi.exception.ContentException;
import se.josef.cmsapi.model.document.Content;
import se.josef.cmsapi.model.web.ContentForm;
import se.josef.cmsapi.model.web.ContentUpdateForm;
import se.josef.cmsapi.model.web.contentsearch.ContentSearch;
import se.josef.cmsapi.repository.ContentRepository;

import java.util.List;

@Service
@Slf4j
public class ContentService {

    private final ContentRepository contentRepository;

    public ContentService(ContentRepository contentRepository) {
        this.contentRepository = contentRepository;
    }

    /**
     * creates content from contentform and saves it. returns saved content.
     */
    public Content saveContent(ContentForm contentForm) {
        Content content = Content.builder()
                .isPublic(contentForm.isPublic())
                .contentFields(contentForm.getContentFields())
                .name(contentForm.getName())
                .projectId(contentForm.getProjectId())
                .templateId(contentForm.getTemplateId())
                .build();
        return contentRepository.save(content);
    }

    /**
     * returns all content with isPublic true
     */
    public List<Content> getAllPublicContent() {
        return contentRepository.findByIsPublicTrue();
    }

    /**
     * finds specific content by id if it is public
     */
    public Content getContentByIdAndPublic(String id) {
        return contentRepository
                .findByIdAndIsPublicTrue(id)
                .orElseThrow(
                        () -> new ContentException(String.format("Content with id %s is unavailable", id))
                );
    }

    /**
     * finds specified content by id
     */
    public Content getById(String id) {
        return contentRepository
                .findById(id)
                .orElseThrow(
                        () -> new ContentException(String.format("Content with id %s is unavailable", id))
                );
    }

    /**
     * finds all content belonging to a project
     *
     * @param projectId
     * @return
     */
    public List<Content> findByProjectId(String projectId) {
        return contentRepository.findByProjectIdOrderByCreatedDesc(projectId);
    }

    /**
     * Asynchronous deletion of all the contents that belong to project
     */
    @Async
    public void deleteByProjectId(String projectId) {
        contentRepository.deleteByProjectId(projectId);
    }

    /**
     * Updates content name, contentfields and isPublic by form values
     */
    //TODO validate form
    public Content updateContent(ContentUpdateForm contentUpdateForm, Content content) {
        try {

            content.setIsPublic(contentUpdateForm.isPublic());
            content.setName(contentUpdateForm.getName());
            content.setContentFields(contentUpdateForm.getContentFields());
            return contentRepository.save(content);

        } catch (Exception e) {
            log.error("error updating project with id {}: {}", contentUpdateForm.getId(), e.getMessage());
            throw new ContentException(String.format("Can't delete project with id %s: %s", contentUpdateForm.getId(), e.getMessage()));
        }
    }

    /**
     * delete content document by its id.
     */
    public void deleteContent(String contentId) {
        try {
            contentRepository.deleteById(contentId);
        } catch (Exception e) {
            log.error("error deleting project with id {}: {}", contentId, e.getMessage());
            throw new ContentException(String.format("Can't delete content with id: %s", contentId));
        }
    }

    /**
     * Searches for content that belongs to project with search criteria specified by searchFields
     */
    public List<Content> searchContent(List<ContentSearch<?>> searchFields, String projectId) {
        return contentRepository.findByProjectIdAndContentFields(searchFields, projectId);
    }

    /**
     * Searches for public content that belongs to project with search criteria specified by searchFields
     */
    public List<Content> searchPublicContent(List<ContentSearch<?>> searchFields) {
        return contentRepository.findByIsPublicAndContentFields(searchFields);
    }

}
