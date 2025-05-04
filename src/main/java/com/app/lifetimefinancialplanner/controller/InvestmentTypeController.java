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

import java.util.List;
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

        InvestmentTypeDTO responseDTO = new InvestmentTypeDTO();
        responseDTO.setId(investmentType.getId());
        responseDTO.setName(investmentType.getName());
        responseDTO.setDescription(investmentType.getDescription());
        responseDTO.setExpenseRatio(investmentType.getExpenseRatio());
        responseDTO.setTaxability(investmentType.getTaxability());

        // Convert Embeddable to DTO
        if (investmentType.getExpectedAnnualReturn() != null) {
            responseDTO.setExpectedAnnualReturn(distributionService.convertEmbeddableToDTO(investmentType.getExpectedAnnualReturn()));
        }
        if (investmentType.getExpectedAnnualIncome() != null) {
            responseDTO.setExpectedAnnualIncome(distributionService.convertEmbeddableToDTO(investmentType.getExpectedAnnualIncome()));
        }

        return ResponseEntity.ok(responseDTO);
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
        Optional<InvestmentType> optional = investmentTypeService.getInvestmentType(id);
        // Raise 404 Error if not found
        if (optional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        InvestmentType investmentType = optional.get();

        // Convert to DTO
        InvestmentTypeDTO responseDTO = new InvestmentTypeDTO();
        responseDTO.setId(investmentType.getId());
        responseDTO.setName(investmentType.getName());
        responseDTO.setDescription(investmentType.getDescription());
        responseDTO.setExpenseRatio(investmentType.getExpenseRatio());
        responseDTO.setTaxability(investmentType.getTaxability());

        // Convert Embeddable to DTO
        if (investmentType.getExpectedAnnualReturn() != null) {
            responseDTO.setExpectedAnnualReturn(
                    distributionService.convertEmbeddableToDTO(investmentType.getExpectedAnnualReturn())
            );
        }
        if (investmentType.getExpectedAnnualIncome() != null) {
            responseDTO.setExpectedAnnualIncome(
                    distributionService.convertEmbeddableToDTO(investmentType.getExpectedAnnualIncome())
            );
        }

        return ResponseEntity.ok(responseDTO);
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

        InvestmentTypeDTO responseDTO = new InvestmentTypeDTO();
        responseDTO.setId(updated.getId());
        responseDTO.setName(updated.getName());
        responseDTO.setDescription(updated.getDescription());
        responseDTO.setExpenseRatio(updated.getExpenseRatio());
        responseDTO.setTaxability(updated.getTaxability());

        // Convert Embeddable to DTO
        if (updated.getExpectedAnnualReturn() != null) {
            responseDTO.setExpectedAnnualReturn(distributionService.convertEmbeddableToDTO(updated.getExpectedAnnualReturn()));
        }
        if (updated.getExpectedAnnualIncome() != null) {
            responseDTO.setExpectedAnnualIncome(distributionService.convertEmbeddableToDTO(updated.getExpectedAnnualIncome()));
        }

        return ResponseEntity.ok(responseDTO);
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

    @GetMapping("/scenario/{scenarioId}")
    @Operation(
            summary = "Get Investment Types for Scenario",
            description = "Retrieves all investment types for a given scenario."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Investment types retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No investment types found for the scenario")
    })
    public ResponseEntity<List<InvestmentTypeDTO>> getInvestmentTypesForScenario(@PathVariable Long scenarioId) {
        List<InvestmentTypeDTO> list = investmentTypeService.getInvestmentTypeList(scenarioId);
        if (list == null || list.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(list);
    }
}
