package se.josef.cmsapi.resource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import se.josef.cmsapi.model.document.User;
import se.josef.cmsapi.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@Slf4j
@RequestMapping(value = "/users", produces = APPLICATION_JSON_VALUE)
@RestController
public class UserResource {

    private final UserService userService;

    public UserResource(UserService userService) {
        this.userService = userService;
    }

    @PostMapping()
    public @ResponseBody
    User addUser(@RequestBody User user) {
        return userService.saveUser(user);
    }

    @GetMapping()
    public @ResponseBody
    List<User> getAllComments() {
        return userService.getAllUsers();
    }

    @CrossOrigin
    @GetMapping(value = "/cross")
    public @ResponseBody
    String checkOrigin(HttpServletRequest request) {
        return request.getHeader("origin");
    }

}
