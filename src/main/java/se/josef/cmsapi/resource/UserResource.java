package se.josef.cmsapi.resource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import se.josef.cmsapi.model.document.User;
import se.josef.cmsapi.model.web.UserForm;
import se.josef.cmsapi.service.UserService;

import javax.validation.constraints.NotNull;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@Slf4j
@RequestMapping(value = "api/user", produces = APPLICATION_JSON_VALUE)
@RestController
public class UserResource {

    private final UserService userService;

    public UserResource(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public @ResponseBody
    User getUser() {
        return userService.getCurrentUser();
    }

    @GetMapping(value = "/{id}")
    public @ResponseBody
    User getUserById(@PathVariable String id) {
        return userService.getById(id);
    }

    @GetMapping(value = "/all")
    public @ResponseBody
    List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping(value = "/signup")
    public @ResponseBody
    User signup(@NotNull @RequestBody UserForm userForm) {
        return userService.signup(userForm);
    }

}
