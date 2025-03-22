package com.app.lifetimefinancialplanner.controller;

import com.app.lifetimefinancialplanner.domain.dto.InvestmentDTO;
//import com.app.lifetimefinancialplanner.service.InvestmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/investments")
@Tag(name = "Investment", description = "API endpoints for Investments")
public class InvestmentController {

//    private final InvestmentService investmentService;

//    public InvestmentController(InvestmentService investmentService) {
//        this.investmentService = investmentService;
//    }

    @Operation(summary = "Create a new Investment", description = "Creates a new investment for a scenario.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Investment created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    public ResponseEntity<InvestmentDTO> createInvestment(@RequestBody InvestmentDTO dto) {
//        InvestmentDTO created = investmentService.createInvestment(dto);
//        return new ResponseEntity<>(created, HttpStatus.CREATED);
        return null;
    }

    @Operation(summary = "Get an Investment", description = "Retrieves an investment by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Investment retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Investment not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<InvestmentDTO> getInvestment(@PathVariable Long id) {
//        InvestmentDTO dto = investmentService.getInvestment(id);
//        return new ResponseEntity<>(dto, HttpStatus.OK);
        return null;
    }
}
