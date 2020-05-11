package se.josef.cmsapi.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import se.josef.cmsapi.exception.ContentException;
import se.josef.cmsapi.model.document.Content;
import se.josef.cmsapi.model.web.ContentForm;
import se.josef.cmsapi.model.web.contentsearch.ContentSearch;
import se.josef.cmsapi.repository.ContentRepository;
import se.josef.cmsapi.util.UserUtils;

import java.util.List;

@Service
@Slf4j
public class ContentService {

    private final ContentRepository contentRepository;
    private final UserUtils userUtils;


    public ContentService(ContentRepository contentRepository, UserUtils userUtils) {
        this.contentRepository = contentRepository;
        this.userUtils = userUtils;
    }

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

    public List<Content> getAllPublicContent() {
        return contentRepository.findByIsPublicTrue();
    }

    public Content getContentByIdAndPublic(String id) {
        return contentRepository
                .findByIdAndIsPublicTrue(id)
                .orElseThrow(
                        () -> new ContentException(String.format("Content with id %s is unavailable", id))
                );
    }

    public Content getById(String id) {
        return contentRepository.findById(id).orElseThrow(
                () -> new ContentException(String.format("Content with id %s is unavailable", id))
        );
    }

    public List<Content> getContentForCurrentUser() {
        return contentRepository
                .findByOwnerIdOrderByCreatedDesc(userUtils.getUserId());
    }

    public List<Content> findByProjectId(String projectId) {
        return contentRepository.findByProjectIdOrderByCreatedDesc(projectId);
    }

    @Async
    public void deleteByProjectId(String projectId) {
        contentRepository.deleteByProjectId(projectId);
    }

    public Content updateContent(ContentForm contentForm, Content content) {
        try {

            content.setIsPublic(contentForm.isPublic());
            content.setName(contentForm.getName());
            content.setContentFields(contentForm.getContentFields());
            return contentRepository.save(content);

        } catch (Exception e) {
            log.error("error updating project with id {}: {}", contentForm.getId(), e.getMessage());
            throw new ContentException(String.format("Can't delete project with id %s: %s", contentForm.getId(), e.getMessage()));
        }
    }

    public boolean deleteContent(String projectId) {
        try {
            contentRepository.deleteById(projectId);
            return true;
        } catch (Exception e) {
            log.error("error deleting project with id {}: {}", projectId, e.getMessage());
            throw new ContentException(String.format("Can't delete content with id: %s", projectId));
        }


    }

    public List<Content> searchContent(List<ContentSearch<?>> searchFields, String projectId) {
        return contentRepository.findByProjectIdAndContentFields(searchFields, projectId);
    }

    public List<Content> searchPublicContent(List<ContentSearch<?>> searchFields) {
        return contentRepository.findByIsPublicAndContentFields(searchFields);
    }

}
