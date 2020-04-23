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

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

}
