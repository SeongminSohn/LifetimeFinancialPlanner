package com.app.lifetimefinancialplanner.controller;

import com.app.lifetimefinancialplanner.domain.dto.ExpenseEventDTO;
//import com.app.lifetimefinancialplanner.service.ExpenseEventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/expense-events")
@Tag(name = "Expense Event", description = "API endpoints for Expense Events")
public class ExpenseEventController {

//    private final ExpenseEventService expenseEventService;
//
//    public ExpenseEventController(ExpenseEventService expenseEventService) {
//        this.expenseEventService = expenseEventService;
//    }

    @Operation(summary = "Create a new Expense Event", description = "Creates a new expense event for the specified scenario.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Expense event created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    public ResponseEntity<ExpenseEventDTO> createExpenseEvent(@RequestBody ExpenseEventDTO expenseEventDTO) {
//        ExpenseEventDTO created = expenseEventService.createExpenseEvent(expenseEventDTO);
//        return new ResponseEntity<>(created, HttpStatus.CREATED);
        return null;
    }

    @Operation(summary = "Get an Expense Event", description = "Retrieves an expense event by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Expense event retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Expense event not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ExpenseEventDTO> getExpenseEvent(@PathVariable Long id) {
//        ExpenseEventDTO dto = expenseEventService.getExpenseEvent(id);
//        return new ResponseEntity<>(dto, HttpStatus.OK);
        return null;
    }
}
