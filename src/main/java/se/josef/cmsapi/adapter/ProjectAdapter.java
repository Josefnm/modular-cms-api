package se.josef.cmsapi.adapter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import se.josef.cmsapi.exception.ProjectException;
import se.josef.cmsapi.service.ContentService;
import se.josef.cmsapi.service.ProjectService;
import se.josef.cmsapi.service.TemplateService;

@Service
@Slf4j
public class ProjectAdapter {

    private final ProjectService projectService;
    private final ContentService contentService;
    private final TemplateService templateService;

    public ProjectAdapter(ProjectService projectService, ContentService contentService, TemplateService templateService) {
        this.projectService = projectService;
        this.contentService = contentService;
        this.templateService = templateService;
    }

    public void deleteProject(String projectId) {
        Long numDeleted = projectService.deleteProject(projectId);
        if (numDeleted > 0) {
            templateService.deleteByProjectId(projectId);
            contentService.deleteByProjectId(projectId);
        } else {
            throw new ProjectException(String.format("Can't delete project with id; %s", projectId));
        }
    }

}
