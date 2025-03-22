package com.app.lifetimefinancialplanner.controller;

import com.app.lifetimefinancialplanner.domain.dto.InflationAssumptionDTO;
import com.app.lifetimefinancialplanner.domain.entity.InflationAssumption;
import com.app.lifetimefinancialplanner.service.InflationAssumptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inflation-assumptions")
@Tag(name = "InflationAssumption API", description = "Endpoints for managing inflation assumptions")
public class InflationAssumptionController {

    private final InflationAssumptionService inflationAssumptionService;

    @Autowired
    public InflationAssumptionController(InflationAssumptionService inflationAssumptionService) {
        this.inflationAssumptionService = inflationAssumptionService;
    }

    @PostMapping
    // Endpoint for creating a new inflation assumption
    @Operation(
            summary = "Create InflationAssumption",
            description = "Creates a new inflation assumption using the provided data. Example JSON: { \"distributionType\": \"FIXED\", \"fixedRate\": 0.03 }"
    )
    public ResponseEntity<InflationAssumption> createInflationAssumption(@RequestBody InflationAssumptionDTO dto) {
        InflationAssumption created = inflationAssumptionService.createInflationAssumption(dto);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    // Endpoint for updating an existing inflation assumption
    @Operation(
            summary = "Update InflationAssumption",
            description = "Updates an existing inflation assumption with the provided data. Example JSON: { \"distributionType\": \"FIXED\", \"fixedRate\": 0.04 }"
    )
    public ResponseEntity<InflationAssumption> updateInflationAssumption(@PathVariable Long id, @RequestBody InflationAssumptionDTO dto) {
        InflationAssumption updated = inflationAssumptionService.updateInflationAssumption(id, dto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    // Endpoint for searching an inflation assumption by ID
    @Operation(
            summary = "Get InflationAssumption",
            description = "Search an inflation assumption by its ID. Example: GET /api/inflation-assumptions/1"
    )
    public ResponseEntity<InflationAssumption> getInflationAssumption(@PathVariable Long id) {
        InflationAssumption assumption = inflationAssumptionService.getInflationAssumption(id);
        return ResponseEntity.ok(assumption);
    }

    @DeleteMapping("/{id}")
    // Endpoint for deleting an inflation assumption by ID
    @Operation(
            summary = "Delete InflationAssumption",
            description = "Deletes an inflation assumption by its ID. Example: DELETE /api/inflation-assumptions/1"
    )
    public ResponseEntity<Void> deleteInflationAssumption(@PathVariable Long id) {
        inflationAssumptionService.deleteInflationAssumption(id);
        return ResponseEntity.noContent().build();
    }
}
