package com.app.lifetimefinancialplanner.controller;

import com.app.lifetimefinancialplanner.domain.dto.InvestEventDTO;
//import com.app.lifetimefinancialplanner.service.InvestEventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invest-events")
@Tag(name = "Invest Event", description = "API endpoints for Invest Events")
public class InvestEventController {

//    private final InvestEventService investEventService;

//    public InvestEventController(InvestEventService investEventService) {
//        this.investEventService = investEventService;
//    }

    @Operation(summary = "Create a new Invest Event", description = "Creates a new invest event for the specified scenario.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Invest event created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    public ResponseEntity<InvestEventDTO> createInvestEvent(@RequestBody InvestEventDTO investEventDTO) {
// TODO: Event SeriesId를 먼저 생성하는 service를 돌리고 그 아이디 담아서 돌아가야됌
    //        InvestEventDTO created = investEventService.createInvestEvent(investEventDTO);
    //        return new ResponseEntity<>(created, HttpStatus.CREATED);
        return null;
    }

    @Operation(summary = "Get an Invest Event", description = "Retrieves an invest event by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Invest event retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Invest event not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<InvestEventDTO> getInvestEvent(@PathVariable Long id) {
//        InvestEventDTO dto = investEventService.getInvestEvent(id);
//        return new ResponseEntity<>(dto, HttpStatus.OK);
        return null;
    }
}
