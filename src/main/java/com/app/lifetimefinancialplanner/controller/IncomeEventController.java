package com.app.lifetimefinancialplanner.controller;

import com.app.lifetimefinancialplanner.domain.entity.IncomeEvent;
import com.app.lifetimefinancialplanner.domain.dto.IncomeEventDTO;
import com.app.lifetimefinancialplanner.service.IncomeEventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/income-events")
@Tag(name = "Income Event API", description = "Endpoints for creating, retrieving, updating, and deleting income events.")
public class IncomeEventController {

    private final IncomeEventService incomeEventService;

    public IncomeEventController(IncomeEventService incomeEventService) {
        this.incomeEventService = incomeEventService;
    }

    // Create a new IncomeEvent
    @PostMapping
    @Operation(
            summary = "Create Income Event",
            description = "Creates a new income event.\n" +
                    "The request body should include the initialAmount and an annualChange object " +
                    "representing the change information.\n\n" +
                    "Example JSON:\n" +
                    "{\n" +
                    "  \"scenarioId\": 1,\n" +
                    "  \"name\": \"Salary\",\n" +
                    "  \"startYear\": 2025,\n" +
                    "  \"duration\": 40,\n" +
                    "  \"eventType\": \"INCOME\",\n" +
                    "  \"initialAmount\": 75000,\n" +
                    "  \"annualChange\": {\n" +
                    "      \"amountOrPercent\": \"AMOUNT\",\n" +
                    "      \"distributionType\": \"UNIFORM\",\n" +
                    "      \"lower\": 500,\n" +
                    "      \"upper\": 2000\n" +
                    "  },\n" +
                    "  \"inflationAdjustment\": \"Y\",\n" +
                    "  \"userPercentage\": 1.0,\n" +
                    "  \"isSocialSecurity\": \"N\"\n" +
                    "}"
    )
    @ApiResponse(responseCode = "201", description = "Income event created successfully",
            content = @Content(schema = @Schema(implementation = IncomeEvent.class)))
    public ResponseEntity<IncomeEvent> createIncomeEvent(@RequestBody IncomeEventDTO incomeEventDTO) {
        IncomeEvent createdIncomeEvent = incomeEventService.createIncomeEvent(incomeEventDTO);
        return ResponseEntity.ok(createdIncomeEvent);
    }

    @GetMapping("/{eventSeriesId}")
    @Operation(
            summary = "Get Income Event",
            description = "Retrieves an income event by its EventSeries ID. Example: GET /api/income-events/1"
    )
    @ApiResponse(responseCode = "200", description = "Income event retrieved successfully",
            content = @Content(schema = @Schema(implementation = IncomeEvent.class)))
    public ResponseEntity<IncomeEvent> getIncomeEvent(@PathVariable Long eventSeriesId) {
        IncomeEvent incomeEvent = incomeEventService.getIncomeEvent(eventSeriesId);
        return ResponseEntity.ok(incomeEvent);
    }

    @PutMapping("/{eventSeriesId}")
    @Operation(
            summary = "Update Income Event",
            description = "Updates an existing income event with the provided fields.\n" +
                    "Example: PUT /api/income-events/1 with JSON body containing fields to update."
    )
    @ApiResponse(responseCode = "200", description = "Income event updated successfully",
            content = @Content(schema = @Schema(implementation = IncomeEvent.class)))
    public ResponseEntity<IncomeEvent> updateIncomeEvent(@PathVariable Long eventSeriesId,
                                                         @RequestBody IncomeEventDTO incomeEventDTO) {
        IncomeEvent updatedIncomeEvent = incomeEventService.updateIncomeEvent(eventSeriesId, incomeEventDTO);
        return ResponseEntity.ok(updatedIncomeEvent);
    }

    @DeleteMapping("/{eventSeriesId}")
    @Operation(
            summary = "Delete Income Event",
            description = "Deletes an existing income event by its EventSeries ID. Example: DELETE /api/income-events/1"
    )
    @ApiResponse(responseCode = "204", description = "Income event deleted successfully")
    public ResponseEntity<Void> deleteIncomeEvent(@PathVariable Long eventSeriesId) {
        incomeEventService.deleteIncomeEvent(eventSeriesId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/scenarios/{scenarioId}")
    @Operation(
            summary = "Get Income Events by Scenario",
            description = "Retrieves all income events for the given scenario ID.\n" +
                    "Example: GET /api/income-events/scenarios/1"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Income events retrieved successfully",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = IncomeEventDTO.class))
            )
    )
    public ResponseEntity<List<IncomeEventDTO>> getIncomeEventsByScenario(
            @PathVariable Long scenarioId) {
        List<IncomeEventDTO> events = incomeEventService.getIncomeEventListByScenarioId(scenarioId);
        return ResponseEntity.ok(events);
    }
}
