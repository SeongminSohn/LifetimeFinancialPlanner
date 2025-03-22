package com.app.lifetimefinancialplanner.controller;

import com.app.lifetimefinancialplanner.domain.dto.LoginDTO;
import com.app.lifetimefinancialplanner.domain.dto.UserDTO;
import com.app.lifetimefinancialplanner.domain.entity.User;
import com.app.lifetimefinancialplanner.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User API", description = "Endpoints for user registration and login")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    // Endpoint for user registration
    @Operation(
            summary = "Register User",
            description = "Creates a new user with email, password, and name. " +
                    "Example request body: { \"email\": \"newuser@example.com\", \"password\": \"pass123\", \"name\": \"NewUser\" }"
    )
    public ResponseEntity<UserDTO> register(@RequestBody UserDTO userDTO) {
        // Call userService to register
        User createdUser = userService.register(userDTO);
        // Convert to DTO for response (password will be hidden due to @JsonProperty)
        UserDTO responseDTO = new UserDTO();
        responseDTO.setId(createdUser.getId());
        responseDTO.setEmail(createdUser.getEmail());
        responseDTO.setName(createdUser.getName());
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/login")
    // Endpoint for user login
    @Operation(
            summary = "Login User",
            description = "Logs in a user using email and password. " +
                    "Example request body: { \"email\": \"user@example.com\", \"password\": \"password123\" }"
    )
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO, HttpSession session) {
        User user = userService.login(loginDTO.getEmail(), loginDTO.getPassword());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
        // Save user info in the Session
        session.setAttribute("loggedInUser", user);
        return ResponseEntity.ok("Login successful");
    }

    @PostMapping("/logout")
    @Operation(
            summary = "Logout User",
            description = "Logs out the user by invalidating the session."
    )
    public ResponseEntity<?> logout(HttpSession session) {
        try {
            session.invalidate();
        } catch (IllegalStateException e) {
            // If the session is already invalidated, do nothing.
        }
        return ResponseEntity.ok("Logout successful");
    }


}
