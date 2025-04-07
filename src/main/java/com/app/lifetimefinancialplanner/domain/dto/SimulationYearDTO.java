package com.app.lifetimefinancialplanner.domain.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SimulationYearDTO {
    private Long id;
    private Integer year;
    private BigDecimal totalInvestments;
    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private BigDecimal totalTax;
    private LocalDateTime createdAt;
}
