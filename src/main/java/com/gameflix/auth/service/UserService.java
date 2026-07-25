package com.gameflix.auth.service;

import com.gameflix.auth.model.User;
import com.gameflix.auth.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    public static class DuplicateUsernameException extends RuntimeException {
        public DuplicateUsernameException(String msg) {
            super(msg);
        }
    }

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Constructor injection for repository and encoder
    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(String username, String rawPassword) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new DuplicateUsernameException("Username already exists");
        }

        // encode raw password before saving; passwordHash is the only place the hash is stored
        User user = new User(username, passwordEncoder.encode(rawPassword));

        userRepository.save(user);
    }

    public boolean authenticate(String username, String rawPassword) {
        return userRepository.findByUsername(username)
                .map(user -> passwordEncoder.matches(rawPassword, user.getPasswordHash()))
                .orElse(false);
    }
}