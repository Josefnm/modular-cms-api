package se.josef.cmsapi.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import se.josef.cmsapi.exception.ContentException;
import se.josef.cmsapi.exception.TemplateException;
import se.josef.cmsapi.model.document.Content;
import se.josef.cmsapi.model.web.ContentForm;
import se.josef.cmsapi.repository.ContentRepository;
import se.josef.cmsapi.repository.ProjectRepository;
import se.josef.cmsapi.util.UserUtils;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class ContentService {

    private final ContentRepository contentRepository;
    private final ProjectRepository projectRepository;
    private final UserUtils userUtils;

    public ContentService(ContentRepository contentRepository, ProjectRepository projectRepository, UserUtils userUtils) {
        this.contentRepository = contentRepository;
        this.projectRepository = projectRepository;
        this.userUtils = userUtils;
    }

    public Content saveContent(ContentForm contentForm) {
        var ownerId = userUtils.getUserId();
        var created = new Date();
        Content content = Content.builder()
                .ownerId(ownerId)
                .created(created)
                .updated(created)
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
                .findByIdAndIsPublicTrue(id, userUtils.getUserId())
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
        var userId = userUtils.getUserId();
        projectRepository
                .findByMemberIdsAndId(userId, projectId)
                .orElseThrow(() ->
                        new TemplateException(String.format("User doesn't have access to project with id: %s", projectId))
                );
        return contentRepository.findByProjectIdOrderByCreatedDesc(projectId);
    }

    public Content updateContent(ContentForm contentForm,Content content) {
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
            throw new ContentException(String.format("Can't delete project with id: %s", projectId));
        }


    }

}
