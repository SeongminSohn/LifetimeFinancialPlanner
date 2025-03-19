package com.app.lifetimefinancialplanner.service;

import com.app.lifetimefinancialplanner.domain.entity.User;
import com.app.lifetimefinancialplanner.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Slf4j
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegister() {
        // given
        User user = User.builder()
                .email("test@example.com")
                .password("password123")
                .name("Test User")
                .build();
        when(userRepository.save(any(User.class))).thenReturn(user);

        // when
        log.info("[testRegister] Before calling userService.register()");
        User savedUser = userService.register(user);

        // then
        log.info("[testRegister] After calling userService.register(), savedUser: {}", savedUser);
        assertNotNull(savedUser);
        assertEquals("test@example.com", savedUser.getEmail());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testLoginSuccess() {
        // given
        User user = User.builder()
                .email("login@example.com")
                .password("password123")
                .name("Login User")
                .build();
        when(userRepository.findByEmail("login@example.com")).thenReturn(user);

        // when
        log.info("[testLoginSuccess] Trying to login with email: login@example.com");
        User result = userService.login("login@example.com", "password123");

        // then
        log.info("[testLoginSuccess] Result user: {}", result);
        assertNotNull(result);
        assertEquals("login@example.com", result.getEmail());
    }

    @Test
    void testLoginFailure() {
        // given
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(null);

        // when
        log.info("[testLoginFailure] Trying to login with nonexistent email");
        User result = userService.login("nonexistent@example.com", "wrongPassword");

        // then
        log.info("[testLoginFailure] Result user: {}", result);
        assertNull(result);
    }
}
