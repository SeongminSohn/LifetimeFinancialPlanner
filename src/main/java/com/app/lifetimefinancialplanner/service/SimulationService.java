package com.app.lifetimefinancialplanner.service;

import com.app.lifetimefinancialplanner.domain.context.SimulationContext;
import com.app.lifetimefinancialplanner.domain.dto.SimulationDTO;
import com.app.lifetimefinancialplanner.domain.entity.Scenario;

public interface SimulationService {
    SimulationDTO runSimulation(Long scenarioId, Integer simulationCount);
    void payExpenseAndTax(Scenario scenario, SimulationContext context, Boolean userAlive, Boolean spouseAlive);
}
