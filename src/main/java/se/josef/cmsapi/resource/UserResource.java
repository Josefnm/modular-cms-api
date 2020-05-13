package se.josef.cmsapi.resource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import se.josef.cmsapi.adapter.UserAdapter;
import se.josef.cmsapi.model.document.User;
import se.josef.cmsapi.model.web.UserForm;
import se.josef.cmsapi.service.UserService;

import javax.validation.constraints.NotNull;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@Slf4j
@RequestMapping(value = "user", produces = APPLICATION_JSON_VALUE)
@RestController
public class UserResource {

    private final UserService userService;
    private final UserAdapter userAdapter;

    public UserResource(UserService userService, UserAdapter userAdapter) {
        this.userService = userService;
        this.userAdapter = userAdapter;
    }

    /**
     * Finds authenticated user by id from security context
     *
     * @return current user
     */
    @GetMapping
    public User getUser() {
        return userService.getCurrentUser();
    }

    /**
     * @param id of the user
     * @return found user
     */
    @GetMapping(value = "/{id}")
    public User getUserById(@PathVariable String id) {
        return userService.getById(id);
    }

    /**
     * @return all users
     */
    @GetMapping(value = "/all")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    /**
     * Search for members that are not part of specified project already
     *
     * @param searchString regex for partial match
     * @param projectId    to exclude users that belong to this project
     * @return users matching search criteria
     */
    @GetMapping(value = "/search")
    public List<User> searchUsersNotInProject(@RequestParam(defaultValue = "") String searchString, @RequestParam String projectId) {
        return userAdapter.searchUsersNotInProject(searchString, projectId);
    }

    /**
     * get users that are members of project
     *
     * @param projectId
     * @return users found
     */
    @GetMapping(value = "/project")
    public List<User> getUsersByProject(@RequestParam String projectId) {
        return userAdapter.findUsersByProject(projectId);
    }

    /**
     * Register a new user
     *
     * @param userForm registration data
     * @return registered user
     */
    @PostMapping(value = "/signup")
    public User signup(@NotNull @RequestBody UserForm userForm) {
        return userService.signup(userForm);
    }

}
