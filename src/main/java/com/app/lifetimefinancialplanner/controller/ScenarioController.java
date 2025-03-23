package com.app.lifetimefinancialplanner.controller;

import com.app.lifetimefinancialplanner.domain.dto.ScenarioDTO;
import com.app.lifetimefinancialplanner.domain.entity.Scenario;
import com.app.lifetimefinancialplanner.service.ScenarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/scenarios")
@Tag(name = "Scenario API", description = "Endpoints for managing scenarios.")
public class ScenarioController {

    private static final Logger log = LoggerFactory.getLogger(ScenarioController.class);
    private final ScenarioService scenarioService;

    @Autowired
    public ScenarioController(ScenarioService scenarioService) {
        this.scenarioService = scenarioService;
    }

    @PostMapping
    // Endpoint for creating a new scenario
    @Operation(
            summary = "Create Scenario",
            description = "Creates a new scenario. \n" +
                    "Example JSON body:\n" +
                    "{\n" +
                    "  \"name\": \"Retirement Plan\",\n" +
                    "  \"maritalStatus\": \"couple\",\n" +
                    "  \"birthYearUser\": 1985,\n" +
                    "  \"birthYearSpouse\": 1987,\n" +
                    "  \"lifeExpectancyUser\": {\"amountOrPercent\": \"AMOUNT\", \"distributionType\": \"FIXED\", \"value\": 80},\n" +
                    "  \"lifeExpectancySpouse\": {\"amountOrPercent\": \"AMOUNT\", \"distributionType\": \"NORMAL\", \"mean\": 82, \"stDev\": 3},\n" +
                    "  \"financialGoal\": 10000,\n" +
                    "  \"preTaxContributionLimit\": 22500,\n" +
                    "  \"afterTaxContributionLimit\": 7000,\n" +
                    "  \"stateOfResidence\": \"NY\",\n" +
                    "  \"inflationAssumption\": {\"amountOrPercent\": \"PERCENT\", \"distributionType\": \"FIXED\", \"value\": 0.03}\n" +
                    "}"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Scenario created successfully",
                    content = @Content(schema = @Schema(implementation = Scenario.class))),
            @ApiResponse(responseCode = "404", description = "User not logged in")
    })
    public ResponseEntity<Scenario> createScenario(@RequestBody ScenarioDTO scenarioDTO, HttpSession session) {
        log.info("sessionUser:"+ session.getAttribute("loggedInUser"));
        Scenario scenario = scenarioService.createScenario(scenarioDTO, session);
        return ResponseEntity.ok(scenario);
    }

    @GetMapping("/{id}")
    // Endpoint for searching a scenario by ID
    @Operation(
            summary = "Get Scenario",
            description = "Search the scenario by its ID. Example: GET /api/scenarios/1"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Scenario retrieved successfully",
                    content = @Content(schema = @Schema(implementation = Scenario.class))),
            @ApiResponse(responseCode = "404", description = "Scenario not found")
    })
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Scenario updated successfully",
                    content = @Content(schema = @Schema(implementation = Scenario.class))),
            @ApiResponse(responseCode = "404", description = "Scenario not found")
    })
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Scenario deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Scenario not found")
    })
    public ResponseEntity<Void> deleteScenario(@PathVariable Long id) {
        scenarioService.deleteScenario(id);
        return ResponseEntity.noContent().build();
    }
}
