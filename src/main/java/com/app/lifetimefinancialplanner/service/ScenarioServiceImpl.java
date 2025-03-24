package com.app.lifetimefinancialplanner.service;

import com.app.lifetimefinancialplanner.domain.dto.ScenarioDTO;
import com.app.lifetimefinancialplanner.domain.embeddable.DistributionEmbeddable;
import com.app.lifetimefinancialplanner.domain.entity.Scenario;
import com.app.lifetimefinancialplanner.domain.entity.User;
import com.app.lifetimefinancialplanner.repository.ScenarioRepository;
import com.app.lifetimefinancialplanner.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ScenarioServiceImpl implements ScenarioService {

    private final ScenarioRepository scenarioRepository;
    private final UserRepository userRepository;
    private final DistributionService distributionService;

    public ScenarioServiceImpl(ScenarioRepository scenarioRepository, UserRepository userRepository, DistributionService distributionService) {
        this.scenarioRepository = scenarioRepository;
        this.userRepository = userRepository;
        this.distributionService = distributionService;
    }

    @Override
    public Scenario createScenario(ScenarioDTO scenarioDTO) {
        // Retrieve the currently logged-in user from HttpSession
        if (scenarioDTO.getUserId() == null) {
            throw new RuntimeException("User not logged in");
        }

        // Use default contribution limits if null
        Double defaultAfterTax = scenarioDTO.getAfterTaxContributionLimit() != null
                ? scenarioDTO.getAfterTaxContributionLimit() : 7000.0;

        // Convert DTO to Embeddable by using helper method
        DistributionEmbeddable lifeExpUser = distributionService.convertDTOToEmbeddable(scenarioDTO.getLifeExpectancyUser());

        DistributionEmbeddable lifeExpSpouse = null;
        Integer birthYearSpouse = null;
        if (!"N".equalsIgnoreCase(scenarioDTO.getMaritalStatus())) {
            lifeExpSpouse = distributionService.convertDTOToEmbeddable(scenarioDTO.getLifeExpectancySpouse());
            birthYearSpouse = scenarioDTO.getBirthYearSpouse();
        }

        DistributionEmbeddable inflationAssumptionEmb = distributionService.convertDTOToEmbeddable(scenarioDTO.getInflationAssumption());

        User user = userRepository.findById(scenarioDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Build a new Scenario using builder pattern
        Scenario scenario = Scenario.builder()
                .user(user)
                .name(scenarioDTO.getName())
                .maritalStatus(scenarioDTO.getMaritalStatus())
                .birthYearUser(scenarioDTO.getBirthYearUser())
                .birthYearSpouse(scenarioDTO.getBirthYearSpouse())
                .lifeExpectancyUser(lifeExpUser)
                .lifeExpectancySpouse(lifeExpSpouse)
                .financialGoal(scenarioDTO.getFinancialGoal())
                .afterTaxContributionLimit(defaultAfterTax)
                .stateOfResidence(scenarioDTO.getStateOfResidence())
                .inflationAssumption(inflationAssumptionEmb)
                .createdAt(LocalDateTime.now())
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

        // Update life expectancy for user if provided
        DistributionEmbeddable updatedLifeExpUser = scenario.getLifeExpectancyUser();
        if (scenarioDTO.getLifeExpectancyUser() != null) {
            updatedLifeExpUser = distributionService.convertDTOToEmbeddable(scenarioDTO.getLifeExpectancyUser());
        }

        DistributionEmbeddable updatedLifeExpSpouse = scenario.getLifeExpectancySpouse();
        if (scenarioDTO.getLifeExpectancySpouse() != null) {
            updatedLifeExpSpouse = distributionService.convertDTOToEmbeddable(scenarioDTO.getLifeExpectancySpouse());
        }

        DistributionEmbeddable updatedInflationAssumption = scenario.getInflationAssumption();
        if (scenarioDTO.getInflationAssumption() != null) {
            updatedInflationAssumption = distributionService.convertDTOToEmbeddable(scenarioDTO.getInflationAssumption());
        }

        // Update Scenario
        Scenario updatedScenario = scenario.toBuilder()
                .name(scenarioDTO.getName() != null ? scenarioDTO.getName() : scenario.getName())
                .maritalStatus(scenarioDTO.getMaritalStatus() != null ? scenarioDTO.getMaritalStatus() : scenario.getMaritalStatus())
                .birthYearUser(scenarioDTO.getBirthYearUser() != null ? scenarioDTO.getBirthYearUser() : scenario.getBirthYearUser())
                .birthYearSpouse(scenarioDTO.getBirthYearSpouse() != null ? scenarioDTO.getBirthYearSpouse() : scenario.getBirthYearSpouse())
                .lifeExpectancyUser(updatedLifeExpUser)
                .lifeExpectancySpouse(updatedLifeExpSpouse)
                .financialGoal(scenarioDTO.getFinancialGoal() != null ? scenarioDTO.getFinancialGoal() : scenario.getFinancialGoal())
                .afterTaxContributionLimit(scenarioDTO.getAfterTaxContributionLimit() != null ? scenarioDTO.getAfterTaxContributionLimit() : scenario.getAfterTaxContributionLimit())
                .stateOfResidence(scenarioDTO.getStateOfResidence() != null ? scenarioDTO.getStateOfResidence() : scenario.getStateOfResidence())
                .inflationAssumption(updatedInflationAssumption)
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
