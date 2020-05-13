package se.josef.cmsapi.resource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.josef.cmsapi.adapter.ProjectAdapter;
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
    private final ProjectAdapter projectAdapter;

    public ProjectResource(ProjectService projectService, ProjectAdapter projectAdapter) {
        this.projectService = projectService;
        this.projectAdapter = projectAdapter;
    }

    /**
     * Saves a new project
     * @param projectForm or creating new template
     * @return the saved template
     */
    @PostMapping()
    public Project saveProject(@RequestBody ProjectForm projectForm) {
        return projectService.saveProject(projectForm);
    }

    /**
     * Updates project with name, description and/or members
     * @param projectForm update data
     * @return updated project
     */
    @PostMapping("/update")
    public Project updateProject(@RequestBody ProjectForm projectForm) {
        return projectService.updateProject(projectForm);
    }

    /**
     * delete project and all its associated contents and templates.
     * @param id for project
     * @return 200 on success
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable String id) {
        projectAdapter.deleteProject(id);
        return ResponseEntity.ok().build();
    }

    /**
     * get project by its id
     * @param id for project
     * @return found project
     */
    @GetMapping("/{id}")
    public Project getProjectById(@PathVariable String id) {
        return projectService.findByIdAndMember(id);
    }

    /**
     * get all projects user is a member of
     * @return found projects
     */
    @GetMapping("/user")
    public List<Project> getProjectsForCurrentUser() {
        return projectService.getProjectsByUserId();
    }

}
