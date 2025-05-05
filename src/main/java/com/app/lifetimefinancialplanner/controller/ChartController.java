package com.app.lifetimefinancialplanner.controller;

import com.app.lifetimefinancialplanner.domain.dto.SimulationDTO;
import com.app.lifetimefinancialplanner.service.SimulationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
    @Operation(
            summary     = "Get Simulation by ID",
            description = "Retrieves a single simulation result by its unique ID."
    )
    @ApiResponse(
            responseCode = "200",
            description  = "Simulation retrieved successfully"
    )
    public ResponseEntity<SimulationDTO> getSimulation(@PathVariable Long simulationId) {
        SimulationDTO dto = simulationService.getSimulation(simulationId);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/scenarios/{scenarioId}/simulations")
    @Operation(
            summary     = "Get Simulations by Scenario",
            description = "Retrieves all simulation results for the given scenario ID, ordered by simulation count."
    )
    @ApiResponse(
            responseCode = "200",
            description  = "Simulations retrieved successfully"
    )
    public ResponseEntity<List<SimulationDTO>> getSimulationsByScenario(@PathVariable Long scenarioId) {
        List<SimulationDTO> simulationDTOList = simulationService.getSimulationsByScenario(scenarioId);
        return ResponseEntity.ok(simulationDTOList);
    }

    @GetMapping("/batches/{batchId}")
    @Operation(
            summary     = "Get Simulations by Batch",
            description = "Retrieves all simulation runs for the given batchId in ascending order."
    )
    @ApiResponse(responseCode = "200", description = "Batch of simulations retrieved successfully")
    public ResponseEntity<List<SimulationDTO>> getSimulationsByBatch(@PathVariable Long batchId) {
        List<SimulationDTO> simulationDTOList = simulationService.getSimulationsByBatch(batchId);
        return ResponseEntity.ok(simulationDTOList);
    }

}