package com.app.lifetimefinancialplanner.service;

import com.app.lifetimefinancialplanner.domain.dto.SimulationDTO;

public interface SimulationService {
    SimulationDTO runSimulation(Long scenarioId, Integer simulationCount);
}
