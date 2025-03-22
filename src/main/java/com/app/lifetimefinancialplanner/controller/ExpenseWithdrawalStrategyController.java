package com.app.lifetimefinancialplanner.controller;

import com.app.lifetimefinancialplanner.domain.dto.ExpenseWithdrawalStrategyDTO;
//import com.app.lifetimefinancialplanner.service.ExpenseWithdrawalStrategyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/expense-withdrawal-strategies")
@Tag(name = "Expense Withdrawal Strategy", description = "API endpoints for Expense Withdrawal Strategies")
public class ExpenseWithdrawalStrategyController {

//    private final ExpenseWithdrawalStrategyService expenseWithdrawalStrategyService;
//
//    public ExpenseWithdrawalStrategyController(ExpenseWithdrawalStrategyService expenseWithdrawalStrategyService) {
//        this.expenseWithdrawalStrategyService = expenseWithdrawalStrategyService;
//    }

    @Operation(summary = "Create a new Expense Withdrawal Strategy", description = "Creates a new expense withdrawal strategy for a scenario.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Expense withdrawal strategy created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    public ResponseEntity<ExpenseWithdrawalStrategyDTO> createExpenseWithdrawalStrategy(@RequestBody ExpenseWithdrawalStrategyDTO dto) {
//        ExpenseWithdrawalStrategyDTO created = expenseWithdrawalStrategyService.createExpenseWithdrawalStrategy(dto);
//        return new ResponseEntity<>(created, HttpStatus.CREATED);
        return null;
    }

    @Operation(summary = "Get an Expense Withdrawal Strategy", description = "Retrieves an expense withdrawal strategy by its strategy ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Expense withdrawal strategy retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Expense withdrawal strategy not found")
    })
    @GetMapping("/{strategyId}")
    public ResponseEntity<ExpenseWithdrawalStrategyDTO> getExpenseWithdrawalStrategy(@PathVariable Long strategyId) {
//        ExpenseWithdrawalStrategyDTO dto = expenseWithdrawalStrategyService.getExpenseWithdrawalStrategy(strategyId);
//        return new ResponseEntity<>(dto, HttpStatus.OK);
        return null;
    }
}
