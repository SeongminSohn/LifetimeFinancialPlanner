package com.app.lifetimefinancialplanner.service;

import com.app.lifetimefinancialplanner.domain.entity.User;
import com.app.lifetimefinancialplanner.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User register(User user) {
        // TODO: Spring Security for password encrypting
        return userRepository.save(user);
    }

    @Override
    public User login(String email, String password) {
        User foundUser = userRepository.findByEmail(email);
        if(foundUser != null && foundUser.getPassword().equals(password)) {
            return foundUser;
        }
        return null;
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
