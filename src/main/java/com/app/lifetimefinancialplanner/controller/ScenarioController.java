package com.app.lifetimefinancialplanner.controller;

import com.app.lifetimefinancialplanner.domain.dto.ScenarioDTO;
import com.app.lifetimefinancialplanner.domain.entity.Scenario;
import com.app.lifetimefinancialplanner.service.ScenarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/scenarios")
@Tag(name = "Scenario API", description = "Endpoints for managing scenarios")
public class ScenarioController {

    private final ScenarioService scenarioService;

    @Autowired
    public ScenarioController(ScenarioService scenarioService) {
        this.scenarioService = scenarioService;
    }

    @PostMapping
    // Endpoint for creating a new scenario
    @Operation(
            summary = "Create Scenario",
            description = "Creates a new scenario. Example JSON: { \"name\": \"Retirement Plan\", \"isMarried\": \"N\", ... }"
    )
    public ResponseEntity<Scenario> createScenario(@RequestBody ScenarioDTO scenarioDTO, HttpSession session) {
        Scenario scenario = scenarioService.createScenario(scenarioDTO, session);
        return ResponseEntity.ok(scenario);
    }

    @GetMapping("/{id}")
    // Endpoint for searching a scenario by ID
    @Operation(
            summary = "Get Scenario",
            description = "Search the scenario by its ID. Example: GET /api/scenarios/1"
    )
    public ResponseEntity<Scenario> getScenario(@PathVariable Long id) {
        Scenario scenario = scenarioService.getScenario(id);
        if (scenario == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(scenario);
    }

    @PutMapping("/{id}")
    // Endpoint for updating an existing scenario
    @Operation(
            summary = "Update Scenario",
            description = "Updates an existing scenario with provided fields. " +
                    "Example: PUT /api/scenarios/1 with JSON body containing partial or full scenario fields."
    )
    public ResponseEntity<Scenario> updateScenario(@PathVariable Long id, @RequestBody ScenarioDTO scenarioDTO) {
        Scenario updatedScenario = scenarioService.updateScenario(id, scenarioDTO);
        return ResponseEntity.ok(updatedScenario);
    }

    @DeleteMapping("/{id}")
    // Endpoint for deleting an existing scenario
    @Operation(
            summary = "Delete Scenario",
            description = "Deletes an existing scenario by ID. Example: DELETE /api/scenarios/1"
    )
    public ResponseEntity<Void> deleteScenario(@PathVariable Long id) {
        scenarioService.deleteScenario(id);
        return ResponseEntity.noContent().build();
    }
}
