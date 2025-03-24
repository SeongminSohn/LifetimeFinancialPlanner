package com.app.lifetimefinancialplanner.service;

import com.app.lifetimefinancialplanner.domain.dto.DistributionDTO;
import com.app.lifetimefinancialplanner.domain.dto.ScenarioDTO;
import com.app.lifetimefinancialplanner.domain.embeddable.DistributionEmbeddable;
import com.app.lifetimefinancialplanner.domain.entity.Scenario;
import com.app.lifetimefinancialplanner.domain.entity.User;
import com.app.lifetimefinancialplanner.repository.ScenarioRepository;
import com.app.lifetimefinancialplanner.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ScenarioServiceImpl implements ScenarioService {

    private static final Logger log = LoggerFactory.getLogger(ScenarioServiceImpl.class);
    @Autowired
    private final ScenarioRepository scenarioRepository;
    @Autowired
    private final UserRepository userRepository;

    @Autowired
    public ScenarioServiceImpl(ScenarioRepository scenarioRepository, UserRepository userRepository) {
        this.scenarioRepository = scenarioRepository;
        this.userRepository = userRepository;
    }

    // Helper Method to process DTO to Embeddable (Handle invalid inputs)
    private DistributionEmbeddable convertDistribution(DistributionDTO dto) {
        if (dto == null) {
            return null;
        }
        DistributionEmbeddable emb = new DistributionEmbeddable();
        emb.setAmountOrPercent(dto.getAmountOrPercent());
        emb.setDistributionType(dto.getDistributionType());
        String type = dto.getDistributionType();
        if ("FIXED".equalsIgnoreCase(type)) {
            emb.setValue(dto.getValue());
            emb.setLower(null);
            emb.setUpper(null);
            emb.setMean(null);
            emb.setStDev(null);
        } else if ("UNIFORM".equalsIgnoreCase(type)) {
            emb.setLower(dto.getLower());
            emb.setUpper(dto.getUpper());
            emb.setValue(null);
            emb.setMean(null);
            emb.setStDev(null);
        } else if ("NORMAL".equalsIgnoreCase(type)) {
            emb.setMean(dto.getMean());
            emb.setStDev(dto.getStDev());
            emb.setValue(null);
            emb.setLower(null);
            emb.setUpper(null);
        } else {
            throw new IllegalArgumentException("Unsupported distribution type: " + type);
        }
        return emb;
    }

    @Override
    public Scenario createScenario(ScenarioDTO scenarioDTO) {
        // Retrieve the currently logged-in user from HttpSession
        if (scenarioDTO.getUserId() == null) {
            throw new RuntimeException("User not logged in");
        }

        // Use default contribution limits if null
        Double defaultPreTax = scenarioDTO.getPreTaxContributionLimit() != null
                ? scenarioDTO.getPreTaxContributionLimit() : 22500.0;
        Double defaultAfterTax = scenarioDTO.getAfterTaxContributionLimit() != null
                ? scenarioDTO.getAfterTaxContributionLimit() : 7000.0;

        // Convert DTO to Embeddable by using helper method
        DistributionEmbeddable lifeExpUser = convertDistribution(scenarioDTO.getLifeExpectancyUser());
        DistributionEmbeddable lifeExpSpouse = convertDistribution(scenarioDTO.getLifeExpectancySpouse());
        DistributionEmbeddable inflationAssumptionEmb = convertDistribution(scenarioDTO.getInflationAssumption());

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
                .preTaxContributionLimit(defaultPreTax)
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
            updatedLifeExpUser = convertDistribution(scenarioDTO.getLifeExpectancyUser());
        }

        DistributionEmbeddable updatedLifeExpSpouse = scenario.getLifeExpectancySpouse();
        if (scenarioDTO.getLifeExpectancySpouse() != null) {
            updatedLifeExpSpouse = convertDistribution(scenarioDTO.getLifeExpectancySpouse());
        }

        DistributionEmbeddable updatedInflationAssumption = scenario.getInflationAssumption();
        if (scenarioDTO.getInflationAssumption() != null) {
            updatedInflationAssumption = convertDistribution(scenarioDTO.getInflationAssumption());
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
                .preTaxContributionLimit(scenarioDTO.getPreTaxContributionLimit() != null ? scenarioDTO.getPreTaxContributionLimit() : scenario.getPreTaxContributionLimit())
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
