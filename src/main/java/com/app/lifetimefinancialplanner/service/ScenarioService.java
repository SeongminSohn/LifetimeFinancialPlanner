package com.app.lifetimefinancialplanner.service;

import com.app.lifetimefinancialplanner.domain.entity.Scenario;
import com.app.lifetimefinancialplanner.domain.dto.ScenarioDTO;

import javax.servlet.http.HttpSession;


public interface ScenarioService {
    Scenario createScenario(ScenarioDTO scenarioDTO, HttpSession httpSession);
    Scenario getScenario(Long scenarioId);
    Scenario updateScenario(Long scenarioId, ScenarioDTO scenarioDTO);
    void deleteScenario(Long scenarioId);
}
