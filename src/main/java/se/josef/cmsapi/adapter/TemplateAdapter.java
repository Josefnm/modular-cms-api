package se.josef.cmsapi.adapter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import se.josef.cmsapi.model.document.Template;
import se.josef.cmsapi.service.ProjectService;
import se.josef.cmsapi.service.TemplateService;

import java.util.List;

@Service
@Slf4j
public class TemplateAdapter extends Adapter {

    private final TemplateService templateService;

    public TemplateAdapter(ProjectService projectService, TemplateService templateService) {
        super(projectService);
        this.templateService = templateService;
    }

    public List<Template> searchByNameAndProjectId(String name, String projectId) {
        return checkIfMemberOfProjectAsync(
                projectId,
                () -> templateService.searchByNameAndProjectId(name, projectId)
        );
    }

    public List<Template> findByProjectId(String projectId) {
        return checkIfMemberOfProjectAsync(
                projectId,
                () -> templateService.findByProjectId(projectId)
        );
    }

}
