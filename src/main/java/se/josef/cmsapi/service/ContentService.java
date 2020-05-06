package se.josef.cmsapi.service;

import org.springframework.stereotype.Service;
import se.josef.cmsapi.exception.ContentException;
import se.josef.cmsapi.model.document.Content;
import se.josef.cmsapi.model.web.ContentForm;
import se.josef.cmsapi.repository.ContentRepository;

import java.util.Date;
import java.util.List;

@Service
public class ContentService {

    private final ContentRepository contentRepository;
    private final UserService userService;


    public ContentService(ContentRepository contentRepository, UserService userService) {
        this.contentRepository = contentRepository;
        this.userService = userService;
    }

    public Content saveContent(ContentForm contentForm) {
        var ownerId = userService.getUserId();
        var created = new Date();
        Content content = Content.builder()
                .ownerId(ownerId)
                .created(created)
                .updated(created)
                .contentFields(contentForm.getContentFields())
                .name(contentForm.getName())
                .projectId(contentForm.getProjectId())
                .templateId(contentForm.getTemplateId())
                .build();
        System.out.println(content.getContentFields().get(0).getClass());
        return contentRepository.save(content);
    }

    public List<Content> getAllPublicContent() {
        return contentRepository.findByIsPublicTrue();
    }

    public Content getContentById(String id) {
        return contentRepository
                .findByIdAndOwnerIdOrIsPublicTrue(id, userService.getUserId())
                .orElseThrow(() ->
                        new ContentException(String.format("Content with id %s is unavailable", id))
                );
    }

    public List<Content> getContentForCurrentUser() {
        return contentRepository
                .findByOwnerIdOrderByCreatedDesc(userService.getUserId());
    }

    public List<Content> findByProjectId(String projectId) {
        return contentRepository.findByProjectIdOrderByCreatedDesc(projectId);
    }

}
