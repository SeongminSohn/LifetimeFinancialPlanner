package com.app.lifetimefinancialplanner.controller;

import com.app.lifetimefinancialplanner.domain.embeddable.DistributionEmbeddable;
import com.app.lifetimefinancialplanner.domain.entity.User;
import com.app.lifetimefinancialplanner.domain.entity.Scenario;
import com.app.lifetimefinancialplanner.domain.entity.InvestmentType;
import com.app.lifetimefinancialplanner.domain.entity.Investment;
import com.app.lifetimefinancialplanner.domain.entity.EventSeries;
import com.app.lifetimefinancialplanner.domain.entity.IncomeEvent;
import com.app.lifetimefinancialplanner.repository.UserRepository;
import com.app.lifetimefinancialplanner.repository.ScenarioRepository;
import com.app.lifetimefinancialplanner.repository.InvestmentTypeRepository;
import com.app.lifetimefinancialplanner.repository.InvestmentRepository;
import com.app.lifetimefinancialplanner.repository.EventSeriesRepository;
import com.app.lifetimefinancialplanner.repository.IncomeEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc
class ScenarioControllerIntegrationTest {

    @Autowired private MockMvc mvc;
    @Autowired private UserRepository userRepository;
    @Autowired private ScenarioRepository scenarioRepository;
    @Autowired private InvestmentTypeRepository investmentTypeRepository;
    @Autowired private InvestmentRepository investmentRepository;
    @Autowired private EventSeriesRepository eventSeriesRepository;
    @Autowired private IncomeEventRepository incomeEventRepository;

    private User testUser;
    private Scenario testScenario;

    @BeforeEach
    void setUp() {
        // Clean up any existing data to start fresh
        incomeEventRepository.deleteAll();
        eventSeriesRepository.deleteAll();
        investmentRepository.deleteAll();
        investmentTypeRepository.deleteAll();
        scenarioRepository.deleteAll();
        userRepository.deleteAll();

        // Insert a User with ID = 1 (auto-generated)
        testUser = User.builder()
                .email("testuser@example.com")
                .password("password")
                .name("Test User")
                .build();
        testUser = userRepository.save(testUser);

        // Insert a Scenario linked to the User
        testScenario = Scenario.builder()
                .user(testUser)
                .name("Test Scenario")
                .maritalStatus("N")  // Single
                .birthYearUser(1990)
                .financialGoal(10000.0)
                .afterTaxContributionLimit(5000.0)
                .stateOfResidence("NY")
                .inflationAssumption(new DistributionEmbeddable() {{
                    setDistributionType("FIXED");
                    setAmountOrPercent("PERCENT");
                    setValue(0.02);
                }})
                .lifeExpectancyUser(new DistributionEmbeddable() {{
                    setDistributionType("FIXED");
                    setAmountOrPercent("AMOUNT");
                    setValue(80.0);
                }})
                .build();
        testScenario = scenarioRepository.save(testScenario);

        // Insert an InvestmentType associated with the Scenario
        InvestmentType investmentType = InvestmentType.builder()
                .scenario(testScenario)
                .name("TEST_TYPE")
                .description("Test Investment Type")
                .expectedAnnualReturn(new DistributionEmbeddable() {{
                    setDistributionType("FIXED");
                    setAmountOrPercent("PERCENT");
                    setValue(0.0);
                }})
                .expectedAnnualIncome(new DistributionEmbeddable() {{
                    setDistributionType("FIXED");
                    setAmountOrPercent("AMOUNT");
                    setValue(0.0);
                }})
                .expenseRatio(0.0)
                .taxability("Y")
                .build();
        investmentType = investmentTypeRepository.save(investmentType);

        // Insert an Investment for the Scenario
        Investment investment = Investment.builder()
                .scenario(testScenario)
                .investmentType(investmentType)
                .taxStatus("NON-RETIREMENT")
                .value(1000.0)
                .build();
        investment = investmentRepository.save(investment);

        // Insert an Income Event (with EventSeries) for the Scenario
        EventSeries incomeSeries = EventSeries.builder()
                .scenario(testScenario)
                .name("Test Income Event")
                .eventType("INCOME")
                .startYear(new DistributionEmbeddable() {{
                    setDistributionType("FIXED");
                    setAmountOrPercent("AMOUNT");
                    setValue(2025.0);
                }})
                .duration(new DistributionEmbeddable() {{
                    setDistributionType("FIXED");
                    setAmountOrPercent("AMOUNT");
                    setValue(5.0);
                }})
                .build();
        incomeSeries = eventSeriesRepository.save(incomeSeries);
        IncomeEvent incomeEvent = IncomeEvent.builder()
                .eventSeries(incomeSeries)
                .initialAmount(50000.0)
                .annualChange(new DistributionEmbeddable() {{
                    setDistributionType("FIXED");
                    setAmountOrPercent("AMOUNT");
                    setValue(0.0);
                }})
                .inflationAdjustment("N")
                .userPercentage(1.0)
                .isSocialSecurity("N")
                .build();
        incomeEventRepository.save(incomeEvent);
    }

    @Test
    void exportScenario_returnsYamlWithNestedData() throws Exception {
        mvc.perform(get("/api/scenarios/{id}/export", testScenario.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/x-yaml"))
                .andExpect(content().string(containsString("name: " + testScenario.getName())))
                .andExpect(content().string(containsString("investmentTypes:")))
                .andExpect(content().string(containsString("investments:")))
                .andExpect(content().string(containsString("eventSeries:")));
    }

    @Test
    void importScenario_createsNewScenarioWithNestedEntities() throws Exception {
        // Load the professor-provided scenario YAML file
        ClassPathResource yamlResource = new ClassPathResource("scenario.yaml");
        byte[] yamlBytes = yamlResource.getInputStream().readAllBytes();
        MockMultipartFile file = new MockMultipartFile("file", "scenario.yaml", "application/x-yaml", yamlBytes);

        // Perform import request with the YAML file and userId
        mvc.perform(multipart("/api/scenarios/import")
                        .file(file)
                        .param("userId", testUser.getId().toString()))
                .andExpect(status().isOk());

        // Verify that a new Scenario with all nested entities is inserted
        Scenario importedScenario = scenarioRepository.findAll().stream()
                .filter(s -> "Retirement Planning Scenario".equals(s.getName()))
                .findFirst()
                .orElse(null);
        assertNotNull(importedScenario, "Imported scenario should exist in the database");
        assertEquals(testUser.getId(), importedScenario.getUser().getId(), "Imported scenario should be linked to the user ID 1");

        // Verify nested InvestmentTypes and Investments were created
        List<InvestmentType> importedTypes = investmentTypeRepository.findAllByScenarioId(importedScenario.getId());
        List<Investment> importedInvestments = investmentRepository.findAllByScenarioId(importedScenario.getId());
        assertFalse(importedTypes.isEmpty(), "InvestmentTypes should be created for imported scenario");
        assertFalse(importedInvestments.isEmpty(), "Investments should be created for imported scenario");

        // Verify nested EventSeries (Income, Expense, and Invest events) were created
        assertFalse(eventSeriesRepository.findAllByScenarioIdAndEventType(importedScenario.getId(), "INCOME").isEmpty(), "Income events should be created");
        assertFalse(eventSeriesRepository.findAllByScenarioIdAndEventType(importedScenario.getId(), "EXPENSE").isEmpty(), "Expense events should be created");
        assertFalse(eventSeriesRepository.findAllByScenarioIdAndEventType(importedScenario.getId(), "INVEST").isEmpty(), "Invest events should be created");
    }
}
