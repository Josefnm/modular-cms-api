package se.josef.cmsapi.adapter;

import org.springframework.stereotype.Service;
import se.josef.cmsapi.exception.TemplateException;
import se.josef.cmsapi.model.document.Project;
import se.josef.cmsapi.model.document.Template;
import se.josef.cmsapi.service.ProjectService;
import se.josef.cmsapi.service.TemplateService;

import java.util.List;

@Service
public class TemplateAdapter {

    private final ProjectService projectService;
    private final TemplateService templateService;

    public TemplateAdapter(ProjectService projectService, TemplateService templateService) {
        this.projectService = projectService;
        this.templateService = templateService;
    }

    public List<Template> searchByNameAndProjectId(String name, String projectId) {
        projectService.getProjectById(projectId);
        System.out.println("here");
        return templateService.searchByNameAndProjectId(name, projectId);
    }

    public List<Template> findByProjectId(String projectId) {
        projectService.getProjectById(projectId);
        return templateService.findByProjectId(projectId);
    }


}
