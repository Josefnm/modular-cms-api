package se.josef.cmsapi.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import se.josef.cmsapi.exception.ProjectException;
import se.josef.cmsapi.model.document.Project;
import se.josef.cmsapi.repository.ProjectRepository;

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
        log.info("here");
        try {
            String uid = userService.getUserId();
            List<Project> res = projectRepository.findAllByMemberIdsOrOwnerIdOrderByCreatedDesc(uid, uid);
            List<Project> res2 = projectRepository.findAllByOwnerIdOrderByCreatedDesc(uid);
            log.info(String.valueOf(res.size()));
            log.info(String.valueOf(res2.size()));
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
