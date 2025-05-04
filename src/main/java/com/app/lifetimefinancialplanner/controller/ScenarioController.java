package com.app.lifetimefinancialplanner.controller;

import com.app.lifetimefinancialplanner.domain.dto.ScenarioDTO;
import com.app.lifetimefinancialplanner.domain.entity.Scenario;
import com.app.lifetimefinancialplanner.service.DistributionService;
import com.app.lifetimefinancialplanner.service.ScenarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/scenarios")
@Tag(name = "Scenario API", description = "Endpoints for managing scenarios.")
public class ScenarioController {

    private final ScenarioService scenarioService;
    private final DistributionService distributionService;

    @Autowired
    public ScenarioController(ScenarioService scenarioService, DistributionService distributionService) {
        this.scenarioService = scenarioService;
        this.distributionService = new DistributionService();
    }

    @PostMapping
    @Operation(
            summary = "Create Scenario",
            description = "Creates a new scenario. \n" +
                    "Example JSON body:\n" +
                    "{\n" +
                    "  \"userId\": 1,\n" +
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
                    content = @Content(schema = @Schema(implementation = ScenarioDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not logged in")
    })
    public ResponseEntity<ScenarioDTO> createScenario(@RequestBody ScenarioDTO scenarioDTO) {
        Scenario scenario = scenarioService.createScenario(scenarioDTO);

        // Create DTO for response
        ScenarioDTO responseDto = new ScenarioDTO();
        responseDto.setUserId(scenario.getUser().getId());
        responseDto.setScenarioId(scenario.getId());
        responseDto.setName(scenario.getName());
        responseDto.setMaritalStatus(scenario.getMaritalStatus());
        responseDto.setBirthYearUser(scenario.getBirthYearUser());
        responseDto.setBirthYearSpouse(scenario.getBirthYearSpouse());
        responseDto.setFinancialGoal(scenario.getFinancialGoal());
        responseDto.setAfterTaxContributionLimit(scenario.getAfterTaxContributionLimit());
        responseDto.setStateOfResidence(scenario.getStateOfResidence());

        // Convert Embeddable to DTO
        if (scenario.getLifeExpectancyUser() != null) {
            responseDto.setLifeExpectancyUser(distributionService.convertEmbeddableToDTO(scenario.getLifeExpectancyUser()));
        }
        if (scenario.getLifeExpectancySpouse() != null) {
            responseDto.setLifeExpectancySpouse(distributionService.convertEmbeddableToDTO(scenario.getLifeExpectancySpouse()));
        }
        if (scenario.getInflationAssumption() != null) {
            responseDto.setInflationAssumption(distributionService.convertEmbeddableToDTO(scenario.getInflationAssumption()));
        }

        return ResponseEntity.ok(responseDto);
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

    @GetMapping("/{id}/export")
    // Endpoint for exporting a scenario to YAML
    @Operation(
            summary = "Export Scenario as YAML",
            description = "Exports the specified scenario and all related data as a downloadable YAML file. " +
                    "Example: GET /api/scenarios/1/export"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "YAML file generated successfully"),
            @ApiResponse(responseCode = "404", description = "Scenario not found")
    })
    public ResponseEntity<Resource> exportYaml(@PathVariable Long id) throws IOException {
        Resource yaml = scenarioService.exportScenarioYaml(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"scenario.yaml\"")
                .contentType(MediaType.parseMediaType("application/x-yaml"))
                .body(yaml);
    }
}
