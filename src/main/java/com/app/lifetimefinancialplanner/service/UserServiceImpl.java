package com.app.lifetimefinancialplanner.service;

import com.app.lifetimefinancialplanner.domain.dto.UserDTO;
import com.app.lifetimefinancialplanner.domain.entity.User;
import com.app.lifetimefinancialplanner.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Register a new User
    @Override
    public User register(UserDTO userDTO) {
        User newUser = User.builder()
                .email(userDTO.getEmail())
                .password(userDTO.getPassword())
                .name(userDTO.getName())
                .createdAt(LocalDateTime.now())
                .build();

        return userRepository.save(newUser);
    }

    // Find User by Id
    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // User Login by email and password
    @Override
    public User login(String email, String password) {
        User foundUser = userRepository.findByEmail(email);
        if (foundUser != null && foundUser.getPassword().equals(password)) {
            return foundUser;
        }
        return null;
    }
}
