package se.josef.cmsapi.service;

import org.springframework.stereotype.Service;
import se.josef.cmsapi.exception.ProjectException;
import se.josef.cmsapi.model.document.Project;
import se.josef.cmsapi.repository.ProjectRepository;

import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserService userService;


    public ProjectService(ProjectRepository projectRepository, UserService userService) {
        this.projectRepository = projectRepository;
        this.userService = userService;
    }

    public Project saveProject(Project Project) {
        return projectRepository.save(Project);
    }

    public Project deleteProject(String projectId) {
        return projectRepository
                .deleteByIdAndOwnerId(projectId, userService.getUserId())
                .orElseThrow(() ->
                        new ProjectException(String.format("Can't delete project with id %s ", projectId))
                );
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public List<Project> getProjectsByUserId() {
        return projectRepository.findAllByMemberIdsOrderByCreatedDesc(userService.getUserId());
    }

    public Project getProjectById(String id) {
        return projectRepository
                .findByIdAndMemberIds(id, userService.getUserId())
                .orElseThrow(() ->
                        new ProjectException(String.format("Project with id %s is unavailable", id))
                );
    }

}
