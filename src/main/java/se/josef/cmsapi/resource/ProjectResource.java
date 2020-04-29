package se.josef.cmsapi.resource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import se.josef.cmsapi.model.document.Project;
import se.josef.cmsapi.model.document.Project;
import se.josef.cmsapi.service.ProjectService;
import se.josef.cmsapi.service.ProjectService;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@Slf4j
@RequestMapping(value = "/project", produces = APPLICATION_JSON_VALUE)
@RestController
public class ProjectResource {

    private final ProjectService projectService;

    public ProjectResource(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping()
    public @ResponseBody
    Project addUser(@RequestBody Project project) {
        return projectService.saveProject(project);
    }

    @GetMapping()
    public @ResponseBody
    List<Project> getAllProject() {
        return projectService.getAllProjects();
    }

    @GetMapping("/getById/{id}")
    public @ResponseBody
    Project getCProjectById(@PathVariable String id) {
        return projectService.getProjectById(id);
    }

    @GetMapping("/getOwn")
    public @ResponseBody
    List<Project> getProjectForCurrentUser() {
        return projectService.getProjectsByUserId();
    }

}
