package com.app.lifetimefinancialplanner.controller;

import com.app.lifetimefinancialplanner.domain.dto.InvestEventDTO;
import com.app.lifetimefinancialplanner.domain.entity.InvestEvent;
import com.app.lifetimefinancialplanner.service.InvestEventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invest-events")
@Tag(name = "Invest Event API", description = "Endpoints for creating, retrieving, updating, and deleting Invest Events")
public class InvestEventController {

    private final InvestEventService investEventService;

    public InvestEventController(InvestEventService investEventService) {
        this.investEventService = investEventService;
    }

    @Operation(
            summary = "Create Invest Event",
            description = "Creates a new invest event. \n" +
                    "Example JSON body:\n" +
                    "{\n" +
                    "  \"scenarioId\": 1,\n" +
                    "  \"eventSeriesId\": 2,\n" +
                    "  \"name\": \"Investment Strategy\",\n" +
                    "  \"startYear\": {\"amountOrPercent\": \"AMOUNT\", \"distributionType\": \"FIXED\", \"value\": 2025},\n" +
                    "  \"duration\": {\"amountOrPercent\": \"AMOUNT\", \"distributionType\": \"FIXED\", \"value\": 30},\n" +
                    "  \"eventType\": \"INVEST\",\n" +
                    "  \"assetAllocation\": {\"amountOrPercent\": \"PERCENT\", \"distributionType\": \"FIXED\", \"value\": 70},\n" +
                    "  \"maxCash\": 5000\n" +
                    "}"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Invest Event created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    public ResponseEntity<InvestEventDTO> createInvestEvent(@RequestBody InvestEventDTO investEventDTO) {
        InvestEvent created = investEventService.createInvestEvent(investEventDTO);
        InvestEventDTO responseDto = new InvestEventDTO();
        responseDto.setEventSeriesId(created.getEventSeriesId());
        if (created.getEventSeries() != null && created.getEventSeries().getScenario() != null) {
            responseDto.setScenarioId(created.getEventSeries().getScenario().getId());
            responseDto.setName(created.getEventSeries().getName());
        }
        responseDto.setMaxCash(created.getMaxCash());
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Get Invest Event",
            description = "Retrieves an invest event by its EventSeries ID. Example: GET /api/invest-events/{id}"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Invest Event retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Invest Event not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<InvestEventDTO> getInvestEvent(@PathVariable Long id) {
        InvestEvent event = investEventService.getInvestEvent(id);
        InvestEventDTO dto = new InvestEventDTO();
        dto.setEventSeriesId(event.getEventSeriesId());
        if (event.getEventSeries() != null && event.getEventSeries().getScenario() != null) {
            dto.setScenarioId(event.getEventSeries().getScenario().getId());
            dto.setName(event.getEventSeries().getName());
        }
        dto.setMaxCash(event.getMaxCash());
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @Operation(
            summary = "Update Invest Event",
            description = "Updates an existing invest event with the provided fields. Example: PUT /api/invest-events/{id}"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Invest Event updated successfully"),
            @ApiResponse(responseCode = "404", description = "Invest Event not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<InvestEventDTO> updateInvestEvent(@PathVariable Long id, @RequestBody InvestEventDTO investEventDTO) {
        InvestEvent updated = investEventService.updateInvestEvent(id, investEventDTO);
        InvestEventDTO dto = new InvestEventDTO();
        dto.setEventSeriesId(updated.getEventSeriesId());
        if (updated.getEventSeries() != null && updated.getEventSeries().getScenario() != null) {
            dto.setScenarioId(updated.getEventSeries().getScenario().getId());
            dto.setName(updated.getEventSeries().getName());
        }
        dto.setMaxCash(updated.getMaxCash());
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @Operation(
            summary = "Delete Invest Event",
            description = "Deletes an invest event by its EventSeries ID. Example: DELETE /api/invest-events/{id}"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Invest Event deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Invest Event not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvestEvent(@PathVariable Long id) {
        investEventService.deleteInvestEvent(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}