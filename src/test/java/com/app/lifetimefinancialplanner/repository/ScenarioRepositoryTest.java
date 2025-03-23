package com.app.lifetimefinancialplanner.repository;

import com.app.lifetimefinancialplanner.domain.entity.Scenario;
import com.app.lifetimefinancialplanner.domain.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ScenarioRepositoryTest {

    @Autowired
    private ScenarioRepository scenarioRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testCreateAndFindScenario() {
        // [EN] Given: create a User first
        User user = User.builder()
                .email("scenarioTest@example.com")
                .password("pass123")
                .name("ScenarioTestUser")
                .build();
        User savedUser = userRepository.save(user);

        // [EN] When: create a new Scenario
        Scenario scenario = Scenario.builder()
                .user(savedUser)
                .name("Test Scenario")
                .maritalStatus("N")
                .birthYearUser(1990)
                .lifeExpectancyUser(85)
                .financialGoal(100000.0)
                .preTaxContributionLimit(20000.0)
                .afterTaxContributionLimit(6000.0)
                .stateOfResidence("NY")
                .build();

        Scenario savedScenario = scenarioRepository.save(scenario);

        // [EN] Then: verify scenario is saved
        assertThat(savedScenario.getId()).isNotNull();

        // [EN] And: find the scenario by ID
        Scenario foundScenario = scenarioRepository.findById(savedScenario.getId()).orElse(null);
        assertThat(foundScenario).isNotNull();
        assertThat(foundScenario.getName()).isEqualTo("Test Scenario");
    }

    @Test
    public void testUpdateScenario() {
        // [EN] Assume a scenario is already saved
        Scenario scenario = Scenario.builder()
                .user(null) // For brevity, we skip user creation here or re-use a user from DB
                .name("Old Name")
                .isMarried("N")
                .birthYearUser(1985)
                .lifeExpectancyUser(80)
                .financialGoal(50000.0)
                .preTaxContributionLimit(18000.0)
                .afterTaxContributionLimit(6000.0)
                .stateOfResidence("NJ")
                .build();
        Scenario saved = scenarioRepository.save(scenario);

        // [EN] Update scenario fields
        saved = saved.toBuilder()
                .name("New Name")
                .financialGoal(99999.0)
                .build();
        Scenario updated = scenarioRepository.save(saved);

        // [EN] Check updated fields
        assertThat(updated.getName()).isEqualTo("New Name");
        assertThat(updated.getFinancialGoal()).isEqualTo(99999.0);
    }

    @Test
    public void testDeleteScenario() {
        // [EN] Create a scenario
        Scenario scenario = Scenario.builder()
                .name("ToDelete")
                .isMarried("N")
                .birthYearUser(1980)
                .lifeExpectancyUser(85)
                .financialGoal(200000.0)
                .preTaxContributionLimit(19500.0)
                .afterTaxContributionLimit(6500.0)
                .stateOfResidence("CT")
                .build();
        Scenario saved = scenarioRepository.save(scenario);

        // [EN] Delete scenario
        scenarioRepository.delete(saved);

        // [EN] Confirm it is gone
        boolean exists = scenarioRepository.existsById(saved.getId());
        assertThat(exists).isFalse();
    }
}
