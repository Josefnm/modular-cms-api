package se.josef.cmsapi.service;

import org.springframework.stereotype.Service;
import se.josef.cmsapi.enums.DataType;
import se.josef.cmsapi.exception.ContentException;
import se.josef.cmsapi.model.document.Content;
import se.josef.cmsapi.model.document.ContentField;
import se.josef.cmsapi.model.web.ContentForm;
import se.josef.cmsapi.repository.ContentRepository;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContentService {

    private final ContentRepository contentRepository;
    private final UserService userService;


    public ContentService(ContentRepository contentRepository, UserService userService) {
        this.contentRepository = contentRepository;
        this.userService = userService;
    }

    public Content saveContent(ContentForm contentForm) {
        String ownerId=userService.getUserId();
        Date created=new Date();
        Content content=Content.builder()
                .ownerId(ownerId)
                .contentFields(contentForm.getContentFields())
                .name(contentForm.getName())
                .description(contentForm.getDescription())
                .created(created)
                .updated(created)
                .projectId(contentForm.getProjectId())
                .templateId(contentForm.getTemplateId())
                .build();
        return contentRepository.save(content);
    }

    public List<Content> getAllContent() {
        return contentRepository.findAll();
    }

    public Content getContentById(String id) {
        return contentRepository
                .findByIdAndOwnerIdOrIsPublic(id, userService.getUserId(), true)
                .orElseThrow(() ->
                        new ContentException(String.format("Content with id %s is unavailable", id))
                );
    }

    public List<Content> getContentForCurrentUser() {
        return contentRepository
                .findByOwnerIdOrderByCreatedDesc(userService.getUserId());
    }

    private <T> List<T> filterContentFieldByType(Content content, DataType dataType) {
        return content
                .getContentFields()
                .stream()
                .filter(contentField -> contentField.getDataType().equals(dataType))
                .map(contentField -> (T) contentField.getData())
                .collect(Collectors.toList());
    }
}
