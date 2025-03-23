package com.app.lifetimefinancialplanner.controller;

import com.app.lifetimefinancialplanner.domain.entity.IncomeEvent;
import com.app.lifetimefinancialplanner.domain.dto.IncomeEventDTO;
import com.app.lifetimefinancialplanner.service.IncomeEventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/income-events")
@Tag(name = "Income Event API", description = "Endpoints for creating and retrieving income events.")
public class IncomeEventController {

    private final IncomeEventService incomeEventService;

    public IncomeEventController(IncomeEventService incomeEventService) {
        this.incomeEventService = incomeEventService;
    }

    // Create a new IncomeEvent
    @PostMapping
    @Operation(
            summary = "Create Income Event",
            description = "Creates a new income event. \n" +
                    "The request body should include the initialAmount and an annualChange object " +
                    "representing the change information. \n\n" +
                    "Example JSON:\n" +
                    "{\n" +
                    "  \"eventSeriesId\": 1,\n" +
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
                    "  \"isSocialSecurity\": \"N\",\n" +
                    "  \"userPercentage\": 1.0\n" +
                    "}"
    )
    @ApiResponse(responseCode = "201", description = "Income event created successfully",
            content = @Content(schema = @Schema(implementation = IncomeEvent.class)))
    public ResponseEntity<IncomeEvent> createIncomeEvent(@RequestBody IncomeEventDTO incomeEventDTO) {
        IncomeEvent createdIncomeEvent = incomeEventService.createIncomeEvent(incomeEventDTO);
        return new ResponseEntity<>(createdIncomeEvent, HttpStatus.CREATED);
    }

    // Get an IncomeEvent by its EventSeries ID
    @GetMapping("/{eventSeriesId}")
    @Operation(
            summary = "Get Income Event",
            description = "Retrieves an income event by its EventSeries ID. Example: GET /api/income-events/1"
    )
    @ApiResponse(responseCode = "200", description = "Income event retrieved successfully",
            content = @Content(schema = @Schema(implementation = IncomeEvent.class)))
    public ResponseEntity<IncomeEvent> getIncomeEvent(@PathVariable Long eventSeriesId) {
        IncomeEvent incomeEvent = incomeEventService.getIncomeEvent(eventSeriesId);
        return new ResponseEntity<>(incomeEvent, HttpStatus.OK);
    }
}
