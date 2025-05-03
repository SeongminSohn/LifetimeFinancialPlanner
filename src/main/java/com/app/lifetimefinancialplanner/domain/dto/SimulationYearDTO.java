package com.app.lifetimefinancialplanner.domain.dto;

import com.app.lifetimefinancialplanner.domain.embeddable.AllocationEmbeddable;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class SimulationYearDTO {
    private Long id;
    private Long simulationId;
    private Integer simulationIndex;
    private Integer year;
    private BigDecimal totalInvestments;
    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private BigDecimal totalTax;
    private BigDecimal cashBalance;
    private List<AllocationEmbeddable> assetAllocations;
    private String details;
    private LocalDateTime createdAt;
}
