package com.app.lifetimefinancialplanner.controller;

import com.app.lifetimefinancialplanner.domain.dto.LoginDTO;
import com.app.lifetimefinancialplanner.domain.dto.UserDTO;
import com.app.lifetimefinancialplanner.domain.entity.User;
import com.app.lifetimefinancialplanner.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User API", description = "Endpoints for user registration and login")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully",
                    content = @Content(schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO, HttpSession session, HttpServletResponse response) {
        User user = userService.login(loginDTO.getEmail(), loginDTO.getPassword());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
        // Save user info in the session
        session.setAttribute("loggedInUser", user);

        // Add JSESSIONID cookie to the response
        Cookie cookie = new Cookie("JSESSIONID", session.getId());
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);

        return ResponseEntity.ok("Login successful");
    }

    @PostMapping("/logout")
    @Operation(
            summary = "Logout User",
            description = "Logs out the user by invalidating the session."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logout successful")
    })
    public ResponseEntity<?> logout(HttpSession session) {
        try {
            session.invalidate();
        } catch (IllegalStateException e) {
            // If the session is already invalidated, do nothing.
        }
        return ResponseEntity.ok("Logout successful");
    }


}
