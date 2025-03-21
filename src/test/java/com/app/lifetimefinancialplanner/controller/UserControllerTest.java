package com.app.lifetimefinancialplanner.controller;

import com.app.lifetimefinancialplanner.domain.dto.LoginDTO;
import com.app.lifetimefinancialplanner.domain.dto.UserDTO;
import com.app.lifetimefinancialplanner.domain.entity.User;
import com.app.lifetimefinancialplanner.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService; // Mock the UserService dependency

    @Autowired
    private ObjectMapper objectMapper;

    // Test for user registration endpoint
    @Test
    public void testRegister() throws Exception {
        // given: Create a UserDTO object for registration
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("testcontroller@example.com");
        userDTO.setPassword("password");
        userDTO.setName("Test Controller User");

        User savedUser = User.builder()
                .id(1L)
                .email("testcontroller@example.com")
                .password("password")
                .name("Test Controller User")
                .build();

        when(userService.register(any(UserDTO.class))).thenReturn(savedUser);

        // when: Perform POST request to /api/users/register
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("testcontroller@example.com"))
                .andExpect(jsonPath("$.name").value("Test Controller User"));
    }

    // Test for successful login endpoint
    @Test
    public void testLoginSuccess() throws Exception {
        // given: Create a LoginDTO with valid credentials
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("logincontroller@example.com");
        loginDTO.setPassword("loginpassword");

        User foundUser = User.builder()
                .id(2L)
                .email("logincontroller@example.com")
                .password("loginpassword")
                .name("Login Controller User")
                .build();

        when(userService.login("logincontroller@example.com", "loginpassword")).thenReturn(foundUser);

        // Create a mock session for storing user info
        MockHttpSession session = new MockHttpSession();

        // when: Perform POST request to /api/users/login
        mockMvc.perform(post("/api/users/login")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("Login successful"));

        // then: Verify that the session now contains the logged in user
        User sessionUser = (User) session.getAttribute("loggedInUser");
        assert sessionUser != null;
        assert sessionUser.getEmail().equals("logincontroller@example.com");
    }

    // Test for login failure with invalid credentials
    @Test
    public void testLoginFailure() throws Exception {
        // given: Create a LoginDTO with invalid credentials
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("failcontroller@example.com");
        loginDTO.setPassword("wrongpassword");

        when(userService.login("failcontroller@example.com", "wrongpassword")).thenReturn(null);

        // when: Perform POST request to /api/users/login expecting unauthorized status
        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid credentials"));
    }

    // Test for logout endpoint
    @Test
    public void testLogout() throws Exception {
        // given: Create a mock session with a logged in user
        MockHttpSession session = new MockHttpSession();
        User user = User.builder()
                .id(3L)
                .email("logoutcontroller@example.com")
                .password("pass")
                .name("Logout Controller User")
                .build();
        session.setAttribute("loggedInUser", user);

        // when: Perform POST request to /api/users/logout
        mockMvc.perform(post("/api/users/logout")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().string("Logout successful"));

        try {
            session.getAttribute("loggedInUser");
            fail("Expected IllegalStateException because session is invalidated");
        } catch (IllegalStateException e) {
            // expected, test passes
        }
    }
}
