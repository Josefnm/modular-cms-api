package se.josef.cmsapi.adapter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import se.josef.cmsapi.model.document.Content;
import se.josef.cmsapi.model.web.ContentForm;
import se.josef.cmsapi.model.web.contentsearch.ContentSearch;
import se.josef.cmsapi.service.ContentService;
import se.josef.cmsapi.service.ProjectService;

import java.util.List;

@Service
@Slf4j
public class ContentAdapter{

    private final ContentService contentService;
    private final ProjectService projectService;

    public ContentAdapter(ContentService contentService, ProjectService projectService) {
        this.contentService = contentService;
        this.projectService = projectService;
    }

    public Content updateContent(ContentForm contentForm) {
        var content = contentService.getById(contentForm.getId());
        projectService.existsByIdAndMember(content.getProjectId());
        return contentService.updateContent(contentForm, content);
    }

    public void deleteContent(String contentId) {
        var content = contentService.getById(contentId);
        projectService.existsByIdAndMember(content.getProjectId());
        contentService.deleteContent(contentId);
    }

    public List<Content> searchContent(List<ContentSearch<?>> searchFields, String projectId) {
        projectService.findByIdAndMember(projectId);
        return contentService.searchContent(searchFields, projectId);
    }

    public List<Content> findByProjectId(String projectId) {
        return projectService.checkIfMemberOfProjectAsync(
                projectId,
                () -> contentService.findByProjectId(projectId)
        );
    }
}
