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

    /**
     * First checks if the user is a member of the project the content belongs to,
     * then updates the content
     */
    public Content updateContent(ContentForm contentForm) {
        var content = contentService.getById(contentForm.getId());
        projectService.existsByIdAndMember(content.getProjectId());
        return contentService.updateContent(contentForm, content);
    }

    /**
     * First checks if the user is a member of the project the content belongs to,
     * then deletes the content
     */
    public void deleteContent(String contentId) {
        var content = contentService.getById(contentId);
        projectService.existsByIdAndMember(content.getProjectId());
        contentService.deleteContent(contentId);
    }

    /**
     * Search for content, restricted by project membership
     */
    public List<Content> searchContent(List<ContentSearch<?>> searchFields, String projectId) {
        projectService.existsByIdAndMember(projectId);
        return projectService.checkIfMemberOfProjectAsync(
                projectId,
                () -> contentService.searchContent(searchFields, projectId)
        );
    }
    /**
     * returns all the projects content if user is a member
     */
    public List<Content> findByProjectId(String projectId) {
        return projectService.checkIfMemberOfProjectAsync(
                projectId,
                () -> contentService.findByProjectId(projectId)
        );
    }
}
