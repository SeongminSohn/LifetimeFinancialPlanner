package com.app.lifetimefinancialplanner.controller;

import com.app.lifetimefinancialplanner.domain.dto.LoginDTO;
import com.app.lifetimefinancialplanner.domain.dto.UserDTO;
import com.app.lifetimefinancialplanner.domain.entity.User;
import com.app.lifetimefinancialplanner.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void testRegister() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("newuser@example.com");
        userDTO.setPassword("newpassword");
        userDTO.setName("New User");

        User user = User.builder()
                .id(1L)
                .email(userDTO.getEmail())
                .password(userDTO.getPassword())
                .name(userDTO.getName())
                .build();

        Mockito.when(userService.register(Mockito.any(User.class))).thenReturn(user);

        log.info("[testRegister] Sending register request with userDTO: {}", userDTO);

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("newuser@example.com"));

        log.info("[testRegister] Register test finished successfully.");
    }

    @Test
    void testLoginSuccess() throws Exception {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("login@example.com");
        loginDTO.setPassword("password123");

        User user = User.builder()
                .id(2L)
                .email("login@example.com")
                .password("password123")
                .name("Login User")
                .build();

        Mockito.when(userService.login("login@example.com", "password123")).thenReturn(user);

        log.info("[testLoginSuccess] Sending login request with loginDTO: {}", loginDTO);

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("login@example.com"));

        log.info("[testLoginSuccess] Login test finished successfully.");
    }

    @Test
    void testLoginFailure() throws Exception {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("fail@example.com");
        loginDTO.setPassword("wrong");

        Mockito.when(userService.login("fail@example.com", "wrong")).thenReturn(null);

        log.info("[testLoginFailure] Sending login request with invalid credentials: {}", loginDTO);

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid credentials"));

        log.info("[testLoginFailure] Login failure test finished successfully.");
    }
}
