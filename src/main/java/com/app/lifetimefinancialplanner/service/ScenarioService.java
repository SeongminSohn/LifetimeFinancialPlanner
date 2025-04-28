package com.app.lifetimefinancialplanner.service;

import com.app.lifetimefinancialplanner.domain.entity.Scenario;
import com.app.lifetimefinancialplanner.domain.dto.ScenarioDTO;

import org.springframework.core.io.Resource;
import java.io.IOException;


public interface ScenarioService {
    Scenario createScenario(ScenarioDTO scenarioDTO);
    Scenario getScenario(Long scenarioId);
    Scenario updateScenario(Long scenarioId, ScenarioDTO scenarioDTO);
    void deleteScenario(Long scenarioId);
    Resource exportScenarioYaml(Long scenarioId) throws IOException;
}
