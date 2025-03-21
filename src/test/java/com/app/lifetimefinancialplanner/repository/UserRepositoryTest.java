package com.app.lifetimefinancialplanner.repository;

import com.app.lifetimefinancialplanner.domain.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    // Test for creating and finding a user by ID and email
    @Test
    public void testCreateAndFindUser() {
        // given: Create a new User entity
        User user = User.builder()
                .email("testrepo@example.com")
                .password("password")  // In production, passwords should be encoded.
                .name("Test Repo User")
                .build();

        User savedUser = userRepository.save(user);
        assertThat(savedUser.getId()).isNotNull();

        // when: Retrieve the user by ID and by email
        User foundById = userRepository.findById(savedUser.getId()).orElse(null);
        User foundByEmail = userRepository.findByEmail("testrepo@example.com");

        // then: Verify that the retrieved user matches the saved user
        assertThat(foundById).isNotNull();
        assertThat(foundByEmail).isNotNull();
        assertThat(foundById.getEmail()).isEqualTo("testrepo@example.com");
        assertThat(foundByEmail.getName()).isEqualTo("Test Repo User");
    }
}
