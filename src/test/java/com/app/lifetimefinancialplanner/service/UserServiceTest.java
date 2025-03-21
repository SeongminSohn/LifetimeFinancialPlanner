package com.app.lifetimefinancialplanner.service;

import com.app.lifetimefinancialplanner.domain.dto.UserDTO;
import com.app.lifetimefinancialplanner.domain.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class UserServiceTest {

    @Autowired
    private UserService userService;

    // Test for registering a new user and then finding the user by email.
    @Test
    public void testRegisterAndFindUser() {
        // Create a new UserDTO
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("testuser@example.com");
        userDTO.setPassword("testpassword");
        userDTO.setName("Test User");

        // Call register method to create a new user.
        User registeredUser = userService.register(userDTO);
        // Assert that the registered user is not null and has an assigned ID.
        assertThat(registeredUser).isNotNull();
        assertThat(registeredUser.getId()).isNotNull();
        assertThat(registeredUser.getEmail()).isEqualTo("testuser@example.com");
        assertThat(registeredUser.getName()).isEqualTo("Test User");

        // Retrieve the user by email
        User foundUser = userService.findByEmail("testuser@example.com");
        // Assert that the user is correctly retrieved.
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getId()).isEqualTo(registeredUser.getId());
    }

    // Test for successful login with valid credentials.
    @Test
    public void testLoginWithValidCredentials() {
        // Create and register a new user.
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("loginuser@example.com");
        userDTO.setPassword("loginpassword");
        userDTO.setName("Login User");

        userService.register(userDTO);

        // Attempt to login with the correct email and password.
        User loggedInUser = userService.login("loginuser@example.com", "loginpassword");
        // Assert that login returns a valid user.
        assertThat(loggedInUser).isNotNull();
        assertThat(loggedInUser.getEmail()).isEqualTo("loginuser@example.com");
    }

    // Test for login failure with invalid credentials.
    @Test
    public void testLoginWithInvalidCredentials() {
        // Create and register a new user.
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("invaliduser@example.com");
        userDTO.setPassword("correctpassword");
        userDTO.setName("Invalid User");

        userService.register(userDTO);

        // Attempt to login with an incorrect password.
        User loggedInUser = userService.login("invaliduser@example.com", "wrongpassword");
        // Assert that login fails and returns null.
        assertThat(loggedInUser).isNull();
    }
}
