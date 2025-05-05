package com.app.lifetimefinancialplanner.service;

import com.app.lifetimefinancialplanner.domain.context.SimulationContext;
import com.app.lifetimefinancialplanner.domain.dto.SimulationDTO;
import com.app.lifetimefinancialplanner.domain.entity.Scenario;

import java.util.List;

public interface SimulationService {
    SimulationDTO getSimulation(Long simulationId);
    List<SimulationDTO> getSimulationsByScenario(Long scenarioId);
    List<SimulationDTO> getSimulationsByBatch(Long batchId);
    List<SimulationDTO> runSimulation(Long scenarioId, Integer simulationCount);
    void payExpenseAndTax(Scenario scenario, SimulationContext context, Boolean userAlive, Boolean spouseAlive);
}
