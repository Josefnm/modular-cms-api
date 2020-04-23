package se.josef.cmsapi.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import se.josef.cmsapi.exception.AuthException;
import se.josef.cmsapi.model.document.User;
import se.josef.cmsapi.model.web.UserForm;
import se.josef.cmsapi.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User signupUser(UserForm userForm) {

        try {

            UserRecord.CreateRequest createRequest = new UserRecord
                    .CreateRequest()
                    .setEmail(userForm.getEmail())
                    .setPassword(userForm.getPassword());

            UserRecord userRecord = FirebaseAuth.getInstance().createUser(createRequest);

            Optional<User> userFromDb = userRepository.findByEmailOrUserName(userForm.getEmail(), userForm.getUserName());

            if (userFromDb.isEmpty()) {
                User user = new User(userRecord.getUid(), userForm.getUserName(), userForm.getEmail());
                User savedUser = userRepository.save(user);
                log.info("Saved user to database with id: {}", savedUser.getId());
                return user;
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

    public String getUserId() {
        FirebaseToken firebaseToken = (FirebaseToken) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getCredentials();
        return firebaseToken.getUid();
    }
}
