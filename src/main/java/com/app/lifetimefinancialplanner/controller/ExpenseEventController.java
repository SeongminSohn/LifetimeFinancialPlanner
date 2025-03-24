package com.app.lifetimefinancialplanner.controller;

import com.app.lifetimefinancialplanner.domain.dto.ExpenseEventDTO;
import com.app.lifetimefinancialplanner.domain.entity.ExpenseEvent;
import com.app.lifetimefinancialplanner.service.ExpenseEventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            summary = "Create a new Expense Event",
            description = "Creates a new expense event for the specified scenario.\n" +
                    "Example JSON body:\n" +
                    "{\n" +
                    "  \"eventSeriesId\": 1,\n" +
                    "  \"name\": \"Food\",\n" +
                    "  \"startYear\": 2025,\n" +
                    "  \"duration\": 40,\n" +
                    "  \"eventType\": \"EXPENSE\",\n" +
                    "  \"initialAmount\": 5000,\n" +
                    "  \"annualChange\": {\"amountOrPercent\": \"AMOUNT\", \"distributionType\": \"FIXED\", \"value\": 1000},\n" +
                    "  \"inflationAdjustment\": \"Y\",\n" +
                    "  \"userPercentage\": 0.5,\n" +
                    "  \"isDiscretionary\": \"Y\"\n" +
                    "}"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Expense event created successfully",
                    content = @Content(schema = @Schema(implementation = ExpenseEvent.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<ExpenseEvent> createExpenseEvent(@RequestBody ExpenseEventDTO expenseEventDTO) {
        ExpenseEvent createdExpenseEvent = expenseEventService.createExpenseEvent(expenseEventDTO);
        return new ResponseEntity<>(createdExpenseEvent, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get an Expense Event",
            description = "Retrieves an expense event by its ID. Example: GET /api/expense-events/1"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Expense event retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ExpenseEvent.class))),
            @ApiResponse(responseCode = "404", description = "Expense event not found")
    })
    public ResponseEntity<ExpenseEvent> getExpenseEvent(@PathVariable Long id) {
        ExpenseEvent expenseEvent = expenseEventService.getExpenseEvent(id);
        return new ResponseEntity<>(expenseEvent, HttpStatus.OK);
    }
}
