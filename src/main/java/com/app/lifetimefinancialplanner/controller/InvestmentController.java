package com.app.lifetimefinancialplanner.controller;

import com.app.lifetimefinancialplanner.domain.dto.InvestmentDTO;
import com.app.lifetimefinancialplanner.domain.entity.Investment;
import com.app.lifetimefinancialplanner.service.InvestmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/investments")
@Tag(name = "Investment API", description = "Endpoints for managing investments")
public class InvestmentController {

    private final InvestmentService investmentService;

    // Constructor injection for InvestmentService
    public InvestmentController(InvestmentService investmentService) {
        this.investmentService = investmentService;
    }

    @PostMapping
    @Operation(
            summary = "Create Investment",
            description = "Creates a new investment for a given scenario."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Investment created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<Investment> createInvestment(@RequestBody InvestmentDTO investmentDTO) {
        Investment created = investmentService.createInvestment(investmentDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get Investment",
            description = "Retrieves an investment by its ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Investment retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Investment not found")
    })
    public ResponseEntity<Investment> getInvestment(@PathVariable Long id) {
        return investmentService.getInvestment(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update Investment",
            description = "Updates an existing investment with provided fields."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Investment updated successfully"),
            @ApiResponse(responseCode = "404", description = "Investment not found")
    })
    public ResponseEntity<Investment> updateInvestment(@PathVariable Long id, @RequestBody InvestmentDTO investmentDTO) {
        Investment updated = investmentService.updateInvestment(id, investmentDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete Investment",
            description = "Deletes an investment by its ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Investment deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Investment not found")
    })
    public ResponseEntity<Void> deleteInvestment(@PathVariable Long id) {
        investmentService.deleteInvestment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/scenario/{scenarioId}")
    @Operation(
            summary = "Get Investments by Scenario",
            description = "Retrieves all investments for a given scenario."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Investments retrieved successfully")
    })
    public ResponseEntity<List<InvestmentDTO>> getInvestmentsByScenario(@PathVariable Long scenarioId) {
        List<InvestmentDTO> list = investmentService.getInvestmentListByScenarioId(scenarioId);
        return ResponseEntity.ok(list);
    }
}
