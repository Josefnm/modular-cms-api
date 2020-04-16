package se.josef.cmsapi.service;

import org.springframework.stereotype.Service;
import se.josef.cmsapi.model.document.User;
import se.josef.cmsapi.repository.UserRepository;

import java.util.List;


@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User saveUser(User comment) {
        return userRepository.save(comment);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
