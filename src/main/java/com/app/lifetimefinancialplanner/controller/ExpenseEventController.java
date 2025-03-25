package com.app.lifetimefinancialplanner.controller;

import com.app.lifetimefinancialplanner.domain.dto.ExpenseEventDTO;
import com.app.lifetimefinancialplanner.domain.entity.ExpenseEvent;
import com.app.lifetimefinancialplanner.service.ExpenseEventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expense-events")
@Tag(name = "Expense Event API", description = "Endpoints for managing expense events.")
public class ExpenseEventController {

    private final ExpenseEventService expenseEventService;

    public ExpenseEventController(ExpenseEventService expenseEventService) {
        this.expenseEventService = expenseEventService;
    }

    @PostMapping
    @Operation(
            summary = "Create Expense Event",
            description = "Creates a new expense event.\n" +
                    "The request body should include the scenarioId, name, startYear, duration, eventType, " +
                    "initialAmount, and an annualChange object that represents the change information, " +
                    "along with inflationAdjustment, userPercentage, and isDiscretionary indicator.\n\n" +
                    "Example JSON:\n" +
                    "{\n" +
                    "  \"scenarioId\": 1,\n" +
                    "  \"name\": \"Food\",\n" +
                    "  \"startYear\": 2025,\n" +
                    "  \"duration\": 200,\n" +
                    "  \"eventType\": \"EXPENSE\",\n" +
                    "  \"initialAmount\": 5000,\n" +
                    "  \"annualChange\": {\n" +
                    "      \"amountOrPercent\": \"PERCENT\",\n" +
                    "      \"distributionType\": \"NORMAL\",\n" +
                    "      \"mean\": 0.02,\n" +
                    "      \"stDev\": 0.01\n" +
                    "  },\n" +
                    "  \"inflationAdjustment\": \"Y\",\n" +
                    "  \"userPercentage\": 0.5,\n" +
                    "  \"isDiscretionary\": \"N\"\n" +
                    "}"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Expense event created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<ExpenseEvent> createExpenseEvent(@RequestBody ExpenseEventDTO expenseEventDTO) {
        ExpenseEvent createdExpenseEvent = expenseEventService.createExpenseEvent(expenseEventDTO);
        return ResponseEntity.ok(createdExpenseEvent);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get Expense Event",
            description = "Retrieves an expense event by its EventSeries ID.\n\n" +
                    "Example: GET /api/expense-events/1"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Expense event retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Expense event not found")
    })
    public ResponseEntity<ExpenseEvent> getExpenseEvent(@PathVariable Long id) {
        ExpenseEvent expenseEvent = expenseEventService.getExpenseEvent(id);
        return ResponseEntity.ok(expenseEvent);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update Expense Event",
            description = "Updates an existing expense event with provided fields.\n" +
                    "The request body may include updated values for initialAmount, annualChange, " +
                    "inflationAdjustment, userPercentage, and isDiscretionary.\n\n" +
                    "Example JSON:\n" +
                    "{\n" +
                    "  \"initialAmount\": 5500,\n" +
                    "  \"annualChange\": {\n" +
                    "      \"amountOrPercent\": \"PERCENT\",\n" +
                    "      \"distributionType\": \"NORMAL\",\n" +
                    "      \"mean\": 0.025,\n" +
                    "      \"stDev\": 0.008\n" +
                    "  },\n" +
                    "  \"inflationAdjustment\": \"Y\",\n" +
                    "  \"userPercentage\": 0.6,\n" +
                    "  \"isDiscretionary\": \"Y\"\n" +
                    "}"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Expense event updated successfully"),
            @ApiResponse(responseCode = "404", description = "Expense event not found")
    })
    public ResponseEntity<ExpenseEvent> updateExpenseEvent(@PathVariable Long id, @RequestBody ExpenseEventDTO expenseEventDTO) {
        ExpenseEvent updatedExpenseEvent = expenseEventService.updateExpenseEvent(id, expenseEventDTO);
        return ResponseEntity.ok(updatedExpenseEvent);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete Expense Event",
            description = "Deletes an expense event by its EventSeries ID.\n\n" +
                    "Example: DELETE /api/expense-events/1"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Expense event deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Expense event not found")
    })
    public ResponseEntity<Void> deleteExpenseEvent(@PathVariable Long id) {
        expenseEventService.deleteExpenseEvent(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/scenario/{scenarioId}")
    @Operation(
            summary = "Get Expense Events by Scenario",
            description = "Retrieves all expense events for a given scenario.\n\n" +
                    "Example: GET /api/expense-events/scenario/1"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Expense events retrieved successfully")
    })
    public ResponseEntity<List<ExpenseEventDTO>> getExpenseEventsByScenario(@PathVariable Long scenarioId) {
        List<ExpenseEventDTO> list = expenseEventService.getExpenseEventsBySeriesId(scenarioId);
        return ResponseEntity.ok(list);
    }
}
