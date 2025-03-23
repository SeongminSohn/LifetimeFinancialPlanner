package com.app.lifetimefinancialplanner.service;

import com.app.lifetimefinancialplanner.domain.dto.DistributionDTO;
import com.app.lifetimefinancialplanner.domain.dto.ScenarioDTO;
import com.app.lifetimefinancialplanner.domain.embeddable.DistributionEmbeddable;
import com.app.lifetimefinancialplanner.domain.entity.Scenario;
import com.app.lifetimefinancialplanner.domain.entity.User;
import com.app.lifetimefinancialplanner.repository.ScenarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

@Service
public class ScenarioServiceImpl implements ScenarioService {

    private static final Logger log = LoggerFactory.getLogger(ScenarioServiceImpl.class);
    @Autowired
    private final ScenarioRepository scenarioRepository;

    @Autowired
    public ScenarioServiceImpl(ScenarioRepository scenarioRepository) {
        this.scenarioRepository = scenarioRepository;
    }

    @Override
    public Scenario createScenario(ScenarioDTO scenarioDTO, HttpSession session) {
        // Retrieve the currently logged-in user from HttpSession
        User user = (User) session.getAttribute("loggedInUser");
        log.info("User: " + user);
        if (user == null) {
            throw new RuntimeException("User not logged in");
        }

        // Use default contribution limits if null
        Double defaultPreTax = scenarioDTO.getPreTaxContributionLimit() != null
                ? scenarioDTO.getPreTaxContributionLimit() : 22500.0;
        Double defaultAfterTax = scenarioDTO.getAfterTaxContributionLimit() != null
                ? scenarioDTO.getAfterTaxContributionLimit() : 7000.0;

        // Convert DistributionDTO for lifeExpectancyuser to DistributionEmbeddable
        DistributionEmbeddable lifeExpUser = null;
        if (scenarioDTO.getLifeExpectancyUser() != null) {
            lifeExpUser = new DistributionEmbeddable();
            lifeExpUser.setAmountOrPercent(scenarioDTO.getLifeExpectancyUser().getAmountOrPercent());
            lifeExpUser.setDistributionType(scenarioDTO.getLifeExpectancyUser().getDistributionType());
            lifeExpUser.setValue(scenarioDTO.getLifeExpectancyUser().getValue());
            lifeExpUser.setLower(scenarioDTO.getLifeExpectancyUser().getLower());
            lifeExpUser.setUpper(scenarioDTO.getLifeExpectancyUser().getUpper());
            lifeExpUser.setMean(scenarioDTO.getLifeExpectancyUser().getMean());
            lifeExpUser.setStDev(scenarioDTO.getLifeExpectancyUser().getStDev());
        }

        // lifeExpectancySpouse
        DistributionEmbeddable lifeExpSpouse = null;
        if (scenarioDTO.getLifeExpectancySpouse() != null) {
            lifeExpSpouse = new DistributionEmbeddable();
            lifeExpSpouse.setAmountOrPercent(scenarioDTO.getLifeExpectancySpouse().getAmountOrPercent());
            lifeExpSpouse.setDistributionType(scenarioDTO.getLifeExpectancySpouse().getDistributionType());
            lifeExpSpouse.setValue(scenarioDTO.getLifeExpectancySpouse().getValue());
            lifeExpSpouse.setLower(scenarioDTO.getLifeExpectancySpouse().getLower());
            lifeExpSpouse.setUpper(scenarioDTO.getLifeExpectancySpouse().getUpper());
            lifeExpSpouse.setMean(scenarioDTO.getLifeExpectancySpouse().getMean());
            lifeExpSpouse.setStDev(scenarioDTO.getLifeExpectancySpouse().getStDev());
        }

        // InflationAssumption
        DistributionEmbeddable inflationAssumptionEmb = null;
        if (scenarioDTO.getInflationAssumption() != null) {
            inflationAssumptionEmb = new DistributionEmbeddable();
            inflationAssumptionEmb.setAmountOrPercent(scenarioDTO.getInflationAssumption().getAmountOrPercent());
            inflationAssumptionEmb.setDistributionType(scenarioDTO.getInflationAssumption().getDistributionType());
            inflationAssumptionEmb.setValue(scenarioDTO.getInflationAssumption().getValue());
            inflationAssumptionEmb.setLower(scenarioDTO.getInflationAssumption().getLower());
            inflationAssumptionEmb.setUpper(scenarioDTO.getInflationAssumption().getUpper());
            inflationAssumptionEmb.setMean(scenarioDTO.getInflationAssumption().getMean());
            inflationAssumptionEmb.setStDev(scenarioDTO.getInflationAssumption().getStDev());
        }

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
            DistributionEmbeddable temp = new DistributionEmbeddable();
            temp.setAmountOrPercent(scenarioDTO.getLifeExpectancyUser().getAmountOrPercent());
            temp.setDistributionType(scenarioDTO.getLifeExpectancyUser().getDistributionType());
            temp.setValue(scenarioDTO.getLifeExpectancyUser().getValue());
            temp.setLower(scenarioDTO.getLifeExpectancyUser().getLower());
            temp.setUpper(scenarioDTO.getLifeExpectancyUser().getUpper());
            temp.setMean(scenarioDTO.getLifeExpectancyUser().getMean());
            temp.setStDev(scenarioDTO.getLifeExpectancyUser().getStDev());
            updatedLifeExpUser = temp;
        }

        // Update life expectancy for spouse if provided
        DistributionEmbeddable updatedLifeExpSpouse = scenario.getLifeExpectancySpouse();
        if (scenarioDTO.getLifeExpectancySpouse() != null) {
            DistributionEmbeddable temp = new DistributionEmbeddable();
            temp.setAmountOrPercent(scenarioDTO.getLifeExpectancySpouse().getAmountOrPercent());
            temp.setDistributionType(scenarioDTO.getLifeExpectancySpouse().getDistributionType());
            temp.setValue(scenarioDTO.getLifeExpectancySpouse().getValue());
            temp.setLower(scenarioDTO.getLifeExpectancySpouse().getLower());
            temp.setUpper(scenarioDTO.getLifeExpectancySpouse().getUpper());
            temp.setMean(scenarioDTO.getLifeExpectancySpouse().getMean());
            temp.setStDev(scenarioDTO.getLifeExpectancySpouse().getStDev());
            updatedLifeExpSpouse = temp;
        }

        // Update inflation assumption if provided
        DistributionEmbeddable updatedInflationAssumption = scenario.getInflationAssumption();
        if (scenarioDTO.getInflationAssumption() != null) {
            DistributionEmbeddable temp = new DistributionEmbeddable();
            temp.setAmountOrPercent(scenarioDTO.getInflationAssumption().getAmountOrPercent());
            temp.setDistributionType(scenarioDTO.getInflationAssumption().getDistributionType());
            temp.setValue(scenarioDTO.getInflationAssumption().getValue());
            temp.setLower(scenarioDTO.getInflationAssumption().getLower());
            temp.setUpper(scenarioDTO.getInflationAssumption().getUpper());
            temp.setMean(scenarioDTO.getInflationAssumption().getMean());
            temp.setStDev(scenarioDTO.getInflationAssumption().getStDev());
            updatedInflationAssumption = temp;
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
