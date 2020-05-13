package se.josef.cmsapi.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import se.josef.cmsapi.exception.ProjectException;
import se.josef.cmsapi.model.document.Project;
import se.josef.cmsapi.model.web.ProjectForm;
import se.josef.cmsapi.model.web.ProjectUpdateForm;
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

    /**
     * save new project from project form
     */
    public Project saveProject(ProjectForm projectForm) {
        var ownerId = userUtils.getUserId();
        var project = Project.builder()
                .name(projectForm.getName())
                .description(projectForm.getDescription())
                .memberIds(Collections.singletonList(ownerId))
                .build();
        return projectRepository.save(project);
    }

    /**
     * deletes project if user is the owner
     */
    public Long deleteProject(String projectId) {
        return projectRepository.deleteByIdAndOwnerId(projectId, userUtils.getUserId());
    }

    /**
     * Returns all projects user is a member of
     */
    public List<Project> getProjectsByUserId() {
        var uid = userUtils.getUserId();
        return projectRepository.findAllByMemberIdsOrderByCreatedDesc(uid, uid);
    }

    /**
     * Returns project if user is a member
     */
    public Project findByIdAndMember(String projectId) {
        var userId = userUtils.getUserId();
        return projectRepository
                .findByMemberIdsAndId(userId, projectId)
                .orElseThrow(() ->
                        new ProjectException(String.format("Project with id %s is unavailable", projectId))
                );
    }

    /**
     * Checks if user is member of project, used for security
     */
    public void existsByIdAndMember(String projectId) {
        var userId = userUtils.getUserId();
        Boolean isMember = projectRepository.existsByMemberIdsAndId(userId, projectId);
        if (!isMember) {
            throw new ProjectException(String.format("User is not a member of project with id: %s ", projectId));
        }
    }

    /**
     * async wrapper for existsByIdAndMember to provide Executor for securitycontext
     */
    @Async
    public CompletableFuture<Void> existsByIdAndMemberAsync(String projectId) {
        existsByIdAndMember(projectId);
        return CompletableFuture.completedFuture(null);
    }

    /**
     * Update name, description and members
     */
    public Project updateProject(ProjectUpdateForm projectUpdateForm) {
        try {
            var project = findByIdAndMember(projectUpdateForm.getId());
            project.setName(projectUpdateForm.getName());
            project.setDescription(projectUpdateForm.getDescription());
            project.setMemberIds(projectUpdateForm.getMemberIds());
            return projectRepository.save(project);

        } catch (Exception e) {
            log.error("updateProject: {}", e.getMessage());
            throw new ProjectException(String.format("Could not update project: %s", e.getMessage()));
        }
    }

    /**
     * Async method used to check if user has access to data retrieved
     * by supplier lambda before returning  the result.
     * The purpose is to improve performance
     *
     * @param projectId project to check that user belongs to
     * @param supplier  method for retrieving requested data
     * @param <T>       type of data requested
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
