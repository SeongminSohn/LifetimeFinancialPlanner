package com.app.lifetimefinancialplanner.service;

import com.app.lifetimefinancialplanner.domain.entity.User;

public interface UserService {
    User register(User user);
    User login(String email, String password);
    User findByEmail(String email);
}
