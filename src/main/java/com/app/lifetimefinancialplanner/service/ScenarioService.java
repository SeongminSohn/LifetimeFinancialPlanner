package com.app.lifetimefinancialplanner.service;

import com.app.lifetimefinancialplanner.domain.dto.ScenarioYamlDTO;
import com.app.lifetimefinancialplanner.domain.entity.Scenario;
import com.app.lifetimefinancialplanner.domain.dto.ScenarioDTO;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


public interface ScenarioService {
    Scenario createScenario(ScenarioDTO scenarioDTO);
    Scenario getScenario(Long scenarioId);
    Scenario updateScenario(Long scenarioId, ScenarioDTO scenarioDTO);
    void deleteScenario(Long scenarioId);
    List<ScenarioDTO> getScenariosByUserId(Long userId);
    ScenarioYamlDTO importScenarioYaml(MultipartFile file, Long userId) throws IOException;
    Resource exportScenarioYaml(Long scenarioId) throws IOException;
}
