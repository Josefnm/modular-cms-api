package se.josef.cmsapi.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import se.josef.cmsapi.exception.ProjectException;
import se.josef.cmsapi.model.document.Project;
import se.josef.cmsapi.model.web.ProjectForm;
import se.josef.cmsapi.repository.ProjectRepository;
import se.josef.cmsapi.util.UserUtils;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

@Service
@Slf4j
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserUtils userUtils;

    public ProjectService(ProjectRepository projectRepository, UserUtils userUtils) {
        this.projectRepository = projectRepository;

        this.userUtils = userUtils;
    }

    public Project saveProject(ProjectForm projectForm) {
        var ownerId = userUtils.getUserId();
        var project = Project.builder()
                .name(projectForm.getName())
                .description(projectForm.getDescription())
                .memberIds(Collections.singletonList(ownerId))
                .build();
        return projectRepository.save(project);
    }

    public Long deleteProject(String projectId) {
        return projectRepository.deleteByIdAndOwnerId(projectId, userUtils.getUserId());
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public List<Project> getProjectsByUserId() {

        var uid = userUtils.getUserId();
        return projectRepository.findAllByMemberIdsOrOwnerIdOrderByCreatedDesc(uid, uid);

    }

    public Project findByIdAndMember(String id) {
        var userId = userUtils.getUserId();
        return projectRepository
                .findByMemberIdsAndId(userId, id)
                .orElseThrow(() ->
                        new ProjectException(String.format("Project with id %s is unavailable", id))
                );
    }

    public void existsByIdAndMember(String id) {
        var userId = userUtils.getUserId();
        Boolean isMember = projectRepository.existsByMemberIdsAndId(userId, id);
        if (!isMember) {
            throw new ProjectException(String.format("User is not a member of project with id: %s ", id));
        }
    }

    /**
     * async wrapper for existsByIdAndMember to provide Executor for securitycontext
     */
    @Async
    public CompletableFuture<Void> existsByIdAndMemberAsync(String id) {
        existsByIdAndMember(id);
        return CompletableFuture.completedFuture(null);
    }

    public Project updateProject(ProjectForm projectForm) {
        try {
            var project = findByIdAndMember(projectForm.getId());
            project.setName(projectForm.getName());
            project.setDescription(projectForm.getDescription());
            project.setMemberIds(projectForm.getMemberIds());
            return projectRepository.save(project);

        } catch (Exception e) {
            log.error("updateProject: {}", e.getMessage());
            throw new ProjectException(String.format("Could not update project: %s", e.getMessage()));
        }
    }

    /**
     * Async method used to check if user has access to supplied
     * data by checking if they are members of the project it belongs to.
     *
     * @param projectId project to check that user belongs to
     * @param supplier  method for retrieving requested data
     * @param <T> type of data requested
     * @return requested data
     */
    public <T> T checkIfMemberOfProjectAsync(String projectId, Supplier<T> supplier) {
        var voidFuture = existsByIdAndMemberAsync(projectId);

        var suppliedFuture = CompletableFuture.supplyAsync(supplier);

        CompletableFuture.allOf(voidFuture, suppliedFuture).join();

        try {
            return suppliedFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("multithreading error: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

}
