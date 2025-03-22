package com.app.lifetimefinancialplanner.controller;

import com.app.lifetimefinancialplanner.domain.dto.SpendingStrategyDTO;
//import com.app.lifetimefinancialplanner.service.SpendingStrategyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/spending-strategies")
@Tag(name = "Spending Strategy", description = "API endpoints for Spending Strategies")
public class SpendingStrategyController {

//    private final SpendingStrategyService spendingStrategyService;
//
//    public SpendingStrategyController(SpendingStrategyService spendingStrategyService) {
//        this.spendingStrategyService = spendingStrategyService;
//    }

    @Operation(summary = "Create a new Spending Strategy", description = "Creates a new spending strategy associated with a scenario.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Spending strategy created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    public ResponseEntity<SpendingStrategyDTO> createSpendingStrategy(@RequestBody SpendingStrategyDTO dto) {
//        SpendingStrategyDTO created = spendingStrategyService.createSpendingStrategy(dto);
//        return new ResponseEntity<>(created, HttpStatus.CREATED);
        return null;
    }

    @Operation(summary = "Get a Spending Strategy", description = "Retrieves a spending strategy by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Spending strategy retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Spending strategy not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<SpendingStrategyDTO> getSpendingStrategy(@PathVariable Long id) {
//        SpendingStrategyDTO dto = spendingStrategyService.getSpendingStrategy(id);
//        return new ResponseEntity<>(dto, HttpStatus.OK);
        return null;
    }
}
