package se.josef.cmsapi.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import se.josef.cmsapi.exception.AuthException;
import se.josef.cmsapi.exception.UserException;
import se.josef.cmsapi.model.document.Project;
import se.josef.cmsapi.model.document.User;
import se.josef.cmsapi.model.web.UserForm;
import se.josef.cmsapi.repository.ProjectRepository;
import se.josef.cmsapi.repository.UserRepository;
import se.josef.cmsapi.util.UserUtils;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final UserUtils userUtils;

    public UserService(UserRepository userRepository, ProjectRepository projectRepository, UserUtils userUtils) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.userUtils = userUtils;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getCurrentUser() {
        return getById(userUtils.getUserId());
    }

    public User getById(String id) {
        return userRepository
                .findById(id)
                .orElseThrow(
                        () -> new UserException("Could not find user profile")
                );
    }

    public User signup(UserForm userForm) {
        try {
            var userFromDb = userRepository.findByEmailOrUserName(userForm.getEmail(), userForm.getUserName());

            if (userFromDb.isEmpty()) {

                var createRequest = new UserRecord
                        .CreateRequest()
                        .setEmail(userForm.getEmail())
                        .setPassword(userForm.getPassword());

                var userRecord = FirebaseAuth.getInstance().createUser(createRequest);
                var user = User.builder()
                        .id(userRecord.getUid())
                        .userName(userForm.getUserName())
                        .email(userForm.getEmail())
                        .build();

                var savedUser = userRepository.save(user);
                log.info("Saved user to database with id: {}", savedUser.getId());
                return savedUser;
            } else {
                if (userFromDb.get().getEmail().equals(userForm.getEmail())) {
                    log.warn("User already exists with email: {}", userForm.getEmail());
                    throw new AuthException("An account is already registered to this email: " + userForm.getEmail());
                } else {
                    log.warn("User already exists with userName: {}", userForm.getUserName());
                    throw new AuthException("User already exists with name: " + userForm.getUserName());
                }
            }
        } catch (FirebaseAuthException exception) {
            log.warn("Error while saving Firebase user - {}", exception.getMessage());
            throw new AuthException("Error creating user in firebase: " + exception.getMessage());
        }
    }

    public List<User> searchUsersNotInProject(String searchString, String projectId) {
        List<User> users;
        if (StringUtils.isNotBlank(searchString)) {
            users = userRepository.searchUsers(searchString);
        } else {
            users = userRepository.findAll();
        }
        var projectOpt = projectRepository.findById(projectId);
        if (projectOpt.isPresent()) {
            return filterUsersNotInProject(users, projectOpt.get());
        }
        return users;
    }

    private List<User> filterUsersNotInProject(List<User> users, Project project) {

        // Hashset for better performance. Would need to add pagination with a large amount of users.
        var memberIds = new HashSet<>(project.getMemberIds());

        return users.stream()
                .filter(user -> !memberIds.contains(user.getId()))
                .collect(Collectors.toList());

    }

    public List<User> findUsersByProject(Project project) {
        return userRepository.findByIdIn(project.getMemberIds());
    }
}
