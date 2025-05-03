package com.app.lifetimefinancialplanner.controller;

import com.app.lifetimefinancialplanner.domain.dto.SimulationDTO;
import com.app.lifetimefinancialplanner.exception.SamplingOverlapException;
import com.app.lifetimefinancialplanner.service.SimulationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/simulations")
@Tag(name = "Simulation API", description = "Endpoints for running simulations and retrieving simulation results")
public class SimulationController {

    private final SimulationService simulationService;

    public SimulationController(SimulationService simulationService) {
        this.simulationService = simulationService;
    }

    @Operation(
            summary = "Run Simulation",
            description = "Executes a simulation for the specified scenario and returns the simulation results, including per-year details."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Simulation executed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    public ResponseEntity<List<SimulationDTO>> runSimulation(@RequestBody SimulationDTO simulationDTO) {
        List<SimulationDTO> simulationDTOResult = simulationService.runSimulation(simulationDTO.getScenarioId(), simulationDTO.getSimulationCount());
        return ResponseEntity.ok(simulationDTOResult);
    }

    @ExceptionHandler(SamplingOverlapException.class)
    public ResponseEntity<String> handleSamplingError(SamplingOverlapException ex) {
        // Raise 422 Unprocessable Entity for Sampling Overlap for Invest Event
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(ex.getMessage());
    }
}
