package com.app.lifetimefinancialplanner.controller;

import com.app.lifetimefinancialplanner.domain.embeddable.DistributionEmbeddable;
import com.app.lifetimefinancialplanner.domain.entity.Scenario;
import com.app.lifetimefinancialplanner.domain.entity.User;
import com.app.lifetimefinancialplanner.repository.ScenarioRepository;
import com.app.lifetimefinancialplanner.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc
class SimulationControllerIntegrationTest {

    @Autowired private MockMvc mvc;
    @Autowired private UserRepository userRepository;
    @Autowired private ScenarioRepository scenarioRepository;

    private Long scenarioId;

    @BeforeEach
    void setUp() {
        // Clean and insert a User and Scenario for simulation
        scenarioRepository.deleteAll();
        userRepository.deleteAll();

        User user = User.builder()
                .email("simuser@example.com")
                .password("password")
                .name("Sim User")
                .build();
        user = userRepository.save(user);

        Scenario scenario = Scenario.builder()
                .user(user)
                .name("Simulation Scenario")
                .maritalStatus("N")
                .birthYearUser(1980)
                .financialGoal(50000.0)
                .afterTaxContributionLimit(7000.0)
                .inflationAssumption(new DistributionEmbeddable() {{
                    setDistributionType("FIXED");
                    setAmountOrPercent("PERCENT");
                    setValue(0.03);
                }})
                .lifeExpectancyUser(new DistributionEmbeddable() {{
                    setDistributionType("FIXED");
                    setAmountOrPercent("AMOUNT");
                    setValue(85.0);
                }})
                .build();
        scenarioId = scenarioRepository.save(scenario).getId();
    }

    @Test
    void runSimulation_returnsSimulationResultDto() throws Exception {
        int simulationCount = 3;
        mvc.perform(post("/api/simulations")
                        .param("scenarioId", scenarioId.toString())
                        .param("simulationCount", String.valueOf(simulationCount)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.simulationCount").value(simulationCount))
                .andExpect(jsonPath("$.simulationYears").isArray());
    }
}
