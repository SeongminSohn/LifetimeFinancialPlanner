package com.app.lifetimefinancialplanner.service;

import com.app.lifetimefinancialplanner.domain.dto.UserDTO;
import com.app.lifetimefinancialplanner.domain.entity.User;

public interface UserService {
    User register(UserDTO userDTO);
    User findByEmail(String email);
    User login(String email, String password);
}
