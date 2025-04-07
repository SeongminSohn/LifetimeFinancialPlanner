package com.app.lifetimefinancialplanner.controller;

import com.app.lifetimefinancialplanner.domain.dto.InvestEventDTO;
import com.app.lifetimefinancialplanner.domain.entity.InvestEvent;
import com.app.lifetimefinancialplanner.service.AllocationService;
import com.app.lifetimefinancialplanner.service.DistributionService;
import com.app.lifetimefinancialplanner.service.InvestEventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invest-events")
@Tag(name = "Invest Event API", description = "Endpoints for managing invest events")
public class InvestEventController {

    private final InvestEventService investEventService;
    private final DistributionService distributionService;
    private final AllocationService allocationService;

    public InvestEventController(InvestEventService investEventService,
                                 DistributionService distributionService,
                                 AllocationService allocationService) {
        this.investEventService = investEventService;
        this.distributionService = distributionService;
        this.allocationService = allocationService;
    }

    @Operation(
            summary = "Create Invest Event",
            description = "Creates a new invest event for the specified scenario. \n" +
                    "Example JSON body:\n" +
                    "{\n" +
                    "  \"scenarioId\": 1,\n" +
                    "  \"name\": \"Invest Event Name\",\n" +
                    "  \"startYear\": { ... },\n" +
                    "  \"duration\": { ... },\n" +
                    "  \"eventType\": \"INVEST\",\n" +
                    "  \"assetAllocations\": [\n" +
                    "      { \"investmentKey\": \"S&P 500 non-retirement\", \"ratio\": 0.6 },\n" +
                    "      { \"investmentKey\": \"S&P 500 after-tax\", \"ratio\": 0.4 }\n" +
                    "  ],\n" +
                    "  \"maxCash\": 5000,\n" +
                    "  \"investmentId\": 2\n" +
                    "}"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Invest event created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    public ResponseEntity<InvestEvent> createInvestEvent(@RequestBody InvestEventDTO investEventDTO) {
        InvestEvent created = investEventService.createInvestEvent(investEventDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Get Invest Event",
            description = "Retrieves an invest event by its EventSeries ID. Example: GET /api/invest-events/{id}"
    )
    @ApiResponse(responseCode = "200", description = "Invest event retrieved successfully")
    @GetMapping("/{id}")
    public ResponseEntity<InvestEventDTO> getInvestEvent(@PathVariable Long id) {
        InvestEvent event = investEventService.getInvestEvent(id)
                .orElseThrow(() -> new RuntimeException("Invest event not found with id: " + id));
        InvestEventDTO dto = new InvestEventDTO();
        dto.setEventSeriesId(event.getEventSeries().getId());
        dto.setScenarioId(event.getEventSeries().getScenario().getId());
        dto.setName(event.getEventSeries().getName());
        dto.setStartYear(distributionService.convertEmbeddableToDTO(event.getEventSeries().getStartYear()));
        dto.setDuration(distributionService.convertEmbeddableToDTO(event.getEventSeries().getDuration()));
        dto.setEventType(event.getEventSeries().getEventType());
        dto.setMaxCash(event.getMaxCash());
        dto.setAssetAllocations(allocationService.convertEmbeddableListToDTOList(event.getAssetAllocations()));
        dto.setInvestmentId(event.getInvestment().getId());
        return ResponseEntity.ok(dto);
    }

    @Operation(
            summary = "Update Invest Event",
            description = "Updates an existing invest event with the provided fields. Example: PUT /api/invest-events/{id}"
    )
    @ApiResponse(responseCode = "200", description = "Invest event updated successfully")
    @PutMapping("/{id}")
    public ResponseEntity<InvestEvent> updateInvestEvent(@PathVariable Long id, @RequestBody InvestEventDTO investEventDTO) {
        InvestEvent updated = investEventService.updateInvestEvent(id, investEventDTO);
        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "Delete Invest Event",
            description = "Deletes an invest event by its EventSeries ID. Example: DELETE /api/invest-events/{id}"
    )
    @ApiResponse(responseCode = "204", description = "Invest event deleted successfully")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvestEvent(@PathVariable Long id) {
        investEventService.deleteInvestEvent(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Get Invest Events by Scenario",
            description = "Retrieves all invest events for a given scenario. Example: GET /api/invest-events/scenario/{scenarioId}"
    )
    @ApiResponse(responseCode = "200", description = "Invest events retrieved successfully")
    @GetMapping("/scenario/{scenarioId}")
    public ResponseEntity<List<InvestEventDTO>> getInvestEventsByScenario(@PathVariable Long scenarioId) {
        List<InvestEventDTO> dtos = investEventService.getInvestEventsByScenarioId(scenarioId);
        return ResponseEntity.ok(dtos);
    }
}
