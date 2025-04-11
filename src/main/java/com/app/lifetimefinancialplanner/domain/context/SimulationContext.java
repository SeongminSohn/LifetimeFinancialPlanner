package com.app.lifetimefinancialplanner.domain.context;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SimulationContext {
    private int currentYear;
    private double currentInflationRate;
    private double inflationFactor;
    private double cumulativeInflation;
    private double adjustedAfterTaxContributionLimit;
    private BigDecimal curYearIncome = BigDecimal.ZERO;
    private BigDecimal curYearSS = BigDecimal.ZERO;
    private BigDecimal totalInvestments = BigDecimal.ZERO;
    private BigDecimal totalExpenses = BigDecimal.ZERO;
    private BigDecimal totalTax = BigDecimal.ZERO;
    private BigDecimal cashBalance = BigDecimal.ZERO;
    private String details;
    private LocalDateTime timestamp;
}
