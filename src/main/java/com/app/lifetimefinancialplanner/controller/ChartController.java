package com.app.lifetimefinancialplanner.controller;

import com.app.lifetimefinancialplanner.domain.dto.SimulationDTO;
import com.app.lifetimefinancialplanner.service.SimulationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/charts")
@RequiredArgsConstructor
public class ChartController {
    private final SimulationService simulationService;

    @GetMapping("/simulations/{simulationId}")
    public ResponseEntity<SimulationDTO> getSimulation(
            @PathVariable Long simulationId) {
        return ResponseEntity.ok(simulationService.getSimulation(simulationId));
    }

    @GetMapping("/scenarios/{scenarioId}/simulations")
    public ResponseEntity<List<SimulationDTO>> getSimulationsByScenario(
            @PathVariable Long scenarioId) {
        return ResponseEntity.ok(simulationService.getSimulationsByScenario(scenarioId));
    }

}