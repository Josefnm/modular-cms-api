package se.josef.cmsapi.adapter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import se.josef.cmsapi.exception.UserException;
import se.josef.cmsapi.model.document.User;
import se.josef.cmsapi.service.ProjectService;
import se.josef.cmsapi.service.UserService;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class UserAdapter {

    private final ProjectService projectService;
    private final UserService userService;
    private final TaskExecutor taskExecutor;

    public UserAdapter(ProjectService projectService, UserService userService, TaskExecutor taskExecutor) {
        this.projectService = projectService;
        this.userService = userService;
        this.taskExecutor = taskExecutor;
    }

    /**
     * Finds all users that are members of project
     */
    public List<User> findUsersByProject(String projectId) {
        var project = projectService.findByIdAndMember(projectId);
        return userService.findUsersByProject(project);
    }

    /**
     * regex search on user name/email, excludes the ones that are already members
     */
    public List<User> searchUsersNotInProject(String searchRegex, String projectId) {

        var usersFuture = CompletableFuture.supplyAsync(() -> userService.searchUsers(searchRegex));
        var projectFuture = CompletableFuture.supplyAsync(() -> projectService.findByIdAndMember(projectId), taskExecutor);
        CompletableFuture.allOf(usersFuture, projectFuture).join();
        try {
            return userService.filterUsersNotInProject(usersFuture.get(), projectFuture.get());
        } catch (InterruptedException | ExecutionException e) {
            log.error("searchUsersNotInProject {}", e.getMessage());
            throw new UserException(e.getMessage());
        }
    }

}
