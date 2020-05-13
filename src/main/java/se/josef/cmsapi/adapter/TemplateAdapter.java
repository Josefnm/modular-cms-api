package se.josef.cmsapi.adapter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import se.josef.cmsapi.model.document.Template;
import se.josef.cmsapi.service.ProjectService;
import se.josef.cmsapi.service.TemplateService;

import java.util.List;

@Service
@Slf4j
public class TemplateAdapter {

    private final TemplateService templateService;
    private final ProjectService projectService;

    public TemplateAdapter(ProjectService projectService, TemplateService templateService) {
        this.projectService = projectService;
        this.templateService = templateService;
    }

    /**
     * regex search on template name, restricted by project membership
     */
    public List<Template> searchByNameAndProjectId(String name, String projectId) {
        return projectService.checkIfMemberOfProjectAsync(
                projectId,
                () -> templateService.searchByNameAndProjectId(name, projectId)
        );
    }

    /**
     * returns templates for project if user is a member
     */
    public List<Template> findByProjectId(String projectId) {
        return projectService.checkIfMemberOfProjectAsync(
                projectId,
                () -> templateService.findByProjectId(projectId)
        );
    }

}
