package se.josef.cmsapi.adapter;

import org.springframework.stereotype.Service;
import se.josef.cmsapi.model.document.Project;
import se.josef.cmsapi.model.document.User;
import se.josef.cmsapi.service.ProjectService;
import se.josef.cmsapi.service.UserService;

import java.util.List;

@Service
public class UserAdapter {

    private final ProjectService projectService;
    private final UserService userService;

    public UserAdapter(ProjectService projectService, UserService userService) {
        this.projectService = projectService;
        this.userService = userService;
    }

    public List<User> findUsersByProject(String projectId) {
        Project project = projectService.getProjectById(projectId);
        return userService.findUsersByProject(project);
    }


}
