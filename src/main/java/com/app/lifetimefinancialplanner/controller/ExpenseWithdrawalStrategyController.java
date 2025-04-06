package com.app.lifetimefinancialplanner.controller;

import com.app.lifetimefinancialplanner.domain.dto.ExpenseWithdrawalStrategyDTO;
import com.app.lifetimefinancialplanner.domain.entity.ExpenseWithdrawalStrategy;
import com.app.lifetimefinancialplanner.service.ExpenseWithdrawalStrategyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/expense-withdrawal-strategies")
@Tag(name = "Expense Withdrawal Strategy API",
        description = "Endpoints for managing expense withdrawal strategies (investment selling order)")
public class ExpenseWithdrawalStrategyController {

    private final ExpenseWithdrawalStrategyService strategyService;

    public ExpenseWithdrawalStrategyController(ExpenseWithdrawalStrategyService strategyService) {
        this.strategyService = strategyService;
    }

    @Operation(
            summary = "Create Expense Withdrawal Strategy",
            description = "Creates a new expense withdrawal strategy with the given scenarioId and sellingOrder.\n" +
                    "Example JSON body:\n" +
                    "{\n" +
                    "  \"scenarioId\": 1,\n" +
                    "  \"sellingOrder\": \"[1,3,5,7]\"\n" +
                    "}"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Expense withdrawal strategy created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    public ResponseEntity<ExpenseWithdrawalStrategyDTO> createExpenseWithdrawalStrategy(@RequestBody ExpenseWithdrawalStrategyDTO dto) {
        ExpenseWithdrawalStrategy strategy = strategyService.createExpenseWithdrawalStrategy(dto);
        ExpenseWithdrawalStrategyDTO responseDto = new ExpenseWithdrawalStrategyDTO();
        responseDto.setId(strategy.getId());
        responseDto.setScenarioId(strategy.getScenarioId());
        responseDto.setSellingOrder(strategy.getSellingOrder());
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Get Expense Withdrawal Strategy",
            description = "Retrieves an expense withdrawal strategy by its ID. Example: GET /api/expense-withdrawal-strategies/{id}"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Expense withdrawal strategy retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Expense withdrawal strategy not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ExpenseWithdrawalStrategyDTO> getExpenseWithdrawalStrategy(@PathVariable Long id) {
        ExpenseWithdrawalStrategy strategy = strategyService.getExpenseWithdrawalStrategy(id);
        ExpenseWithdrawalStrategyDTO responseDto = new ExpenseWithdrawalStrategyDTO();
        responseDto.setId(strategy.getId());
        responseDto.setScenarioId(strategy.getScenarioId());
        responseDto.setSellingOrder(strategy.getSellingOrder());
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @Operation(
            summary = "Update Expense Withdrawal Strategy",
            description = "Updates an existing expense withdrawal strategy's selling order. Example: PUT /api/expense-withdrawal-strategies/{id}"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Expense withdrawal strategy updated successfully"),
            @ApiResponse(responseCode = "404", description = "Expense withdrawal strategy not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ExpenseWithdrawalStrategyDTO> updateExpenseWithdrawalStrategy(@PathVariable Long id, @RequestBody ExpenseWithdrawalStrategyDTO dto) {
        ExpenseWithdrawalStrategy updated = strategyService.updateExpenseWithdrawalStrategy(id, dto);
        ExpenseWithdrawalStrategyDTO responseDto = new ExpenseWithdrawalStrategyDTO();
        responseDto.setId(updated.getId());
        responseDto.setScenarioId(updated.getScenarioId());
        responseDto.setSellingOrder(updated.getSellingOrder());
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @Operation(
            summary = "Delete Expense Withdrawal Strategy",
            description = "Deletes an expense withdrawal strategy by its ID. Example: DELETE /api/expense-withdrawal-strategies/{id}"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Expense withdrawal strategy deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Expense withdrawal strategy not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpenseWithdrawalStrategy(@PathVariable Long id) {
        strategyService.deleteExpenseWithdrawalStrategy(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

//    @Operation(
//            summary = "Get Expense Withdrawal Strategies by Scenario",
//            description = "Retrieves all expense withdrawal strategies for a given scenario. Example: GET /api/expense-withdrawal-strategies/scenario/{scenarioId}"
//    )
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Expense withdrawal strategies retrieved successfully")
//    })
//    @GetMapping("/scenario/{scenarioId}")
//    public ResponseEntity<List<ExpenseWithdrawalStrategyDTO>> getStrategiesByScenario(@PathVariable Long scenarioId) {
//        List<ExpenseWithdrawalStrategy> strategies = strategyService.getExpenseWithdrawalStrategiesByScenarioId(scenarioId);
//        List<ExpenseWithdrawalStrategyDTO> dtos = strategies.stream().map(strategy -> {
//            ExpenseWithdrawalStrategyDTO dto = new ExpenseWithdrawalStrategyDTO();
//            dto.setId(strategy.getId());
//            dto.setScenarioId(strategy.getScenarioId());
//            dto.setSellingOrder(strategy.getSellingOrder());
//            return dto;
//        }).collect(Collectors.toList());
//        return new ResponseEntity<>(dtos, HttpStatus.OK);
//    }
}
