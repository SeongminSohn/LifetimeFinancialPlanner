package com.app.lifetimefinancialplanner.controller;

import com.app.lifetimefinancialplanner.domain.entity.IncomeEvent;
import com.app.lifetimefinancialplanner.domain.dto.IncomeEventDTO;
import com.app.lifetimefinancialplanner.service.IncomeEventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/income-events")
public class IncomeEventController {

    private final IncomeEventService incomeEventService;

    public IncomeEventController(IncomeEventService incomeEventService) {
        this.incomeEventService = incomeEventService;
    }

    // Create a new IncomeEvent
    @PostMapping
    public ResponseEntity<IncomeEvent> createIncomeEvent(@RequestBody IncomeEventDTO incomeEventDTO) {
        IncomeEvent createdIncomeEvent = incomeEventService.createIncomeEvent(incomeEventDTO);
        return new ResponseEntity<>(createdIncomeEvent, HttpStatus.CREATED);
    }

    // Get an IncomeEvent by its EventSeries ID
    @GetMapping("/{eventSeriesId}")
    public ResponseEntity<IncomeEvent> getIncomeEvent(@PathVariable Long eventSeriesId) {
        IncomeEvent incomeEvent = incomeEventService.getIncomeEvent(eventSeriesId);
        return new ResponseEntity<>(incomeEvent, HttpStatus.OK);
    }
}
