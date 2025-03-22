package com.app.lifetimefinancialplanner.service;

import com.app.lifetimefinancialplanner.domain.dto.ScenarioDTO;
import com.app.lifetimefinancialplanner.domain.entity.Scenario;
import com.app.lifetimefinancialplanner.domain.entity.User;
import com.app.lifetimefinancialplanner.repository.ScenarioRepository;
import com.app.lifetimefinancialplanner.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

@Service
public class ScenarioServiceImpl implements ScenarioService {

    @Autowired
    private final ScenarioRepository scenarioRepository;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    public ScenarioServiceImpl(ScenarioRepository scenarioRepository, UserRepository userRepository) {
        this.scenarioRepository = scenarioRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Scenario createScenario(ScenarioDTO scenarioDTO, HttpSession session) {
        // Retrieve the currently logged-in user from HttpSession
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            throw new RuntimeException("User not logged in");
        }

        // Use default contribution limits if null
        Double defaultPreTax = scenarioDTO.getPreTaxContributionLimit() != null
                ? scenarioDTO.getPreTaxContributionLimit() : 22500.0;
        Double defaultAfterTax = scenarioDTO.getAfterTaxContributionLimit() != null
                ? scenarioDTO.getAfterTaxContributionLimit() : 7000.0;

        // Build a new Scenario using builder pattern
        Scenario scenario = Scenario.builder()
                .user(user)
                .name(scenarioDTO.getName())
                .maritalStatus(scenarioDTO.getMaritalStatus())
                .birthYearUser(scenarioDTO.getBirthYearUser())
                .birthYearSpouse(scenarioDTO.getBirthYearSpouse())
                .lifeExpectancyUser(scenarioDTO.getLifeExpectancyUser())
                .lifeExpectancySpouse(scenarioDTO.getLifeExpectancySpouse())
                .financialGoal(scenarioDTO.getFinancialGoal())
                .preTaxContributionLimit(defaultPreTax)
                .afterTaxContributionLimit(defaultAfterTax)
                .createdAt(LocalDateTime.now())
                .stateOfResidence(scenarioDTO.getStateOfResidence())
                .build();

        // Save and return the new scenario
        return scenarioRepository.save(scenario);
    }

    @Override
    public Scenario getScenario(Long id) {
        return scenarioRepository.findById(id).orElse(null);
    }

    @Override
    public Scenario updateScenario(Long scenarioId, ScenarioDTO scenarioDTO) {
        // Check if scenario exists
        Scenario scenario = scenarioRepository.findById(scenarioId)
                .orElseThrow(() -> new RuntimeException("Scenario not found"));

        // Update mutable fields using the Builder's toBuilder method
        Scenario updatedScenario = scenario.toBuilder()
                .name(scenarioDTO.getName() != null ? scenarioDTO.getName() : scenario.getName())
                .maritalStatus(scenarioDTO.getMaritalStatus() != null ? scenarioDTO.getMaritalStatus() : scenario.getMaritalStatus())
                .birthYearUser(scenarioDTO.getBirthYearUser() != null ? scenarioDTO.getBirthYearUser() : scenario.getBirthYearUser())
                .birthYearSpouse(scenarioDTO.getBirthYearSpouse() != null ? scenarioDTO.getBirthYearSpouse() : scenario.getBirthYearSpouse())
                .lifeExpectancyUser(scenarioDTO.getLifeExpectancyUser() != null ? scenarioDTO.getLifeExpectancyUser() : scenario.getLifeExpectancyUser())
                .lifeExpectancySpouse(scenarioDTO.getLifeExpectancySpouse() != null ? scenarioDTO.getLifeExpectancySpouse() : scenario.getLifeExpectancySpouse())
                .financialGoal(scenarioDTO.getFinancialGoal() != null ? scenarioDTO.getFinancialGoal() : scenario.getFinancialGoal())
                .preTaxContributionLimit(scenarioDTO.getPreTaxContributionLimit() != null ? scenarioDTO.getPreTaxContributionLimit() : scenario.getPreTaxContributionLimit())
                .afterTaxContributionLimit(scenarioDTO.getAfterTaxContributionLimit() != null ? scenarioDTO.getAfterTaxContributionLimit() : scenario.getAfterTaxContributionLimit())
                .stateOfResidence(scenarioDTO.getStateOfResidence() != null ? scenarioDTO.getStateOfResidence() : scenario.getStateOfResidence())
                .build();

        return scenarioRepository.save(updatedScenario);
    }

    @Override
    public void deleteScenario(Long scenarioId) {
        // Check if scenario exists
        Scenario scenario = scenarioRepository.findById(scenarioId)
                .orElseThrow(() -> new RuntimeException("Scenario not found for deletion"));

        scenarioRepository.delete(scenario);
    }
}
