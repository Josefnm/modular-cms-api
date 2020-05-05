package se.josef.cmsapi.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import se.josef.cmsapi.exception.ProjectException;
import se.josef.cmsapi.model.document.Project;
import se.josef.cmsapi.model.web.ProjectForm;
import se.josef.cmsapi.repository.ProjectRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserService userService;


    public ProjectService(ProjectRepository projectRepository, UserService userService) {
        this.projectRepository = projectRepository;
        this.userService = userService;
    }

    public Project saveProject(ProjectForm projectForm) {
        var ownerId=userService.getUserId();
        var created=new Date();
        Project project=Project.builder()
                .ownerId(ownerId)
                .name(projectForm.getName())
                .description(projectForm.getDescription())
                .memberIds(new ArrayList<>())
                .created(created)
                .updated(created)
                .build();
        return projectRepository.save(project);
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
        try {
            String uid = userService.getUserId();
            return projectRepository.findAllByMemberIdsOrOwnerIdOrderByCreatedDesc(uid, uid);
        } catch (Exception e) {
            log.debug(e.getMessage());
            throw e;
        }
    }

    public Project getProjectById(String id) {
        return projectRepository
                .findByIdAndMemberIds(id, userService.getUserId())
                .orElseThrow(() ->
                        new ProjectException(String.format("Project with id %s is unavailable", id))
                );
    }

}
