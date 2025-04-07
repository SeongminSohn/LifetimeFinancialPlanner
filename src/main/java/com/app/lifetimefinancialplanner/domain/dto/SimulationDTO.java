package com.app.lifetimefinancialplanner.domain.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class SimulationDTO {
    private Long id;
    private Long scenarioId;
    private Integer simulationCount;
    private String result;
    private LocalDateTime createdAt;
    private List<SimulationYearDTO> simulationYears;
}
