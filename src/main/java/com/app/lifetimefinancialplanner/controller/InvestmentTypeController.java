package com.app.lifetimefinancialplanner.controller;

import com.app.lifetimefinancialplanner.domain.dto.InvestmentTypeDTO;
import com.app.lifetimefinancialplanner.domain.entity.InvestmentType;
import com.app.lifetimefinancialplanner.service.DistributionService;
import com.app.lifetimefinancialplanner.service.InvestmentTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/investment-types")
@Tag(name = "Investment Type API", description = "Endpoints for managing investment types")
public class InvestmentTypeController {

    private final InvestmentTypeService investmentTypeService;
    private final DistributionService distributionService;

    public InvestmentTypeController(InvestmentTypeService investmentTypeService, DistributionService distributionService) {
        this.investmentTypeService = investmentTypeService;
        this.distributionService = distributionService;
    }

    @PostMapping
    @Operation(
            summary = "Create Investment Type",
            description = "Creates a new investment type. Example JSON body:\n" +
                    "{\n" +
                    "  \"name\": \"S&P 500\",\n" +
                    "  \"description\": \"S&P 500 index fund\",\n" +
                    "  \"expectedAnnualReturn\": { ... },\n" +
                    "  \"expenseRatio\": 0.001,\n" +
                    "  \"expectedAnnualIncome\": { ... },\n" +
                    "  \"taxability\": \"Y\"\n" +
                    "}"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Investment type created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<InvestmentTypeDTO> createInvestmentType(@RequestBody InvestmentTypeDTO investmentTypeDTO) {
        InvestmentType investmentType = investmentTypeService.createInvestmentType(investmentTypeDTO);

        InvestmentTypeDTO responseDto = new InvestmentTypeDTO();
        responseDto.setId(investmentType.getId());
        responseDto.setName(investmentType.getName());
        responseDto.setDescription(investmentType.getDescription());
        responseDto.setExpenseRatio(investmentType.getExpenseRatio());
        responseDto.setTaxability(investmentType.getTaxability());

        // Convert Embeddable to DTO
        if (investmentType.getExpectedAnnualReturn() != null) {
            responseDto.setExpectedAnnualReturn(distributionService.convertEmbeddableToDTO(investmentType.getExpectedAnnualReturn()));
        }
        if (investmentType.getExpectedAnnualIncome() != null) {
            responseDto.setExpectedAnnualReturn(distributionService.convertEmbeddableToDTO(investmentType.getExpectedAnnualIncome()));
        }

        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get Investment Type",
            description = "Retrieves an investment type by its ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Investment type retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Investment type not found")
    })
    public ResponseEntity<InvestmentTypeDTO> getInvestmentType(@PathVariable Long id) {
        return null;
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update Investment Type",
            description = "Updates an existing investment type with provided fields."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Investment type updated successfully"),
            @ApiResponse(responseCode = "404", description = "Investment type not found")
    })
    public ResponseEntity<InvestmentTypeDTO> updateInvestmentType(@PathVariable Long id, @RequestBody InvestmentTypeDTO investmentTypeDTO) {
        InvestmentType updated = investmentTypeService.updateInvestmentType(id, investmentTypeDTO);

        InvestmentTypeDTO responseDto = new InvestmentTypeDTO();
        responseDto.setId(updated.getId());
        responseDto.setName(updated.getName());
        responseDto.setDescription(updated.getDescription());
        responseDto.setExpenseRatio(updated.getExpenseRatio());
        responseDto.setTaxability(updated.getTaxability());

        // Convert Embeddable to DTO
        if (updated.getExpectedAnnualReturn() != null) {
            responseDto.setExpectedAnnualReturn(distributionService.convertEmbeddableToDTO(updated.getExpectedAnnualReturn()));
        }
        if (updated.getExpectedAnnualIncome() != null) {
            responseDto.setExpectedAnnualReturn(distributionService.convertEmbeddableToDTO(updated.getExpectedAnnualIncome()));
        }

        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete Investment Type",
            description = "Deletes an investment type by its ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Investment type deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Investment type not found")
    })
    public ResponseEntity<Void> deleteInvestmentType(@PathVariable Long id) {
        investmentTypeService.deleteInvestmentType(id);
        return ResponseEntity.noContent().build();
    }
}
