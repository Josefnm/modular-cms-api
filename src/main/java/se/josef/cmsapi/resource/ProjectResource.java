package se.josef.cmsapi.resource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import se.josef.cmsapi.model.document.Project;
import se.josef.cmsapi.model.web.ProjectForm;
import se.josef.cmsapi.service.ProjectService;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@Slf4j
@RequestMapping(value = "project", produces = APPLICATION_JSON_VALUE)
@RestController
public class ProjectResource {

    private final ProjectService projectService;

    public ProjectResource(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping()
    public Project addProject(@RequestBody ProjectForm projectForm) {
        return projectService.saveProject(projectForm);
    }

    @PostMapping("/update")
    public Project updateProject(@RequestBody ProjectForm projectForm) {
        return projectService.updateProject(projectForm);
    }

    @DeleteMapping("/{id}")
    public boolean deleteProject(@PathVariable String id) {
        return projectService.deleteProject(id);
    }

    @GetMapping()
    public List<Project> getAllProject() {
        return projectService.getAllProjects();
    }

    @GetMapping("/{id}")
    public Project getProjectById(@PathVariable String id) {
        return projectService.getProjectById(id);
    }

    @GetMapping("/user")
    public List<Project> getProjectForCurrentUser() {
        return projectService.getProjectsByUserId();
    }

}
