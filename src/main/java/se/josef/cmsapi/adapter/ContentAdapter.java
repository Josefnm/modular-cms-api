package se.josef.cmsapi.adapter;

import org.springframework.stereotype.Service;
import se.josef.cmsapi.model.document.Content;
import se.josef.cmsapi.model.web.ContentForm;
import se.josef.cmsapi.model.web.contentsearch.ContentSearch;
import se.josef.cmsapi.service.ContentService;
import se.josef.cmsapi.service.ProjectService;

import java.util.List;

@Service
public class ContentAdapter {

    private final ProjectService projectService;
    private final ContentService contentService;

    public ContentAdapter(ProjectService projectService, ContentService contentService) {
        this.projectService = projectService;
        this.contentService = contentService;
    }

    public Content updateContent(ContentForm contentForm) {
        var content = contentService.getById(contentForm.getId());
        projectService.getProjectById(content.getProjectId());
        return contentService.updateContent(contentForm,content);
    }

    public boolean deleteContent(String contentId) {
        var content = contentService.getById(contentId);
        //to check if user is member of project
        projectService.getProjectById(content.getProjectId());
        return contentService.deleteContent(contentId);
    }


    public List<Content> searchContent(List<ContentSearch<?>> searchFields, String projectId) {
        projectService.getProjectById(projectId);
        return contentService.searchContent(searchFields,projectId);
    }


}
