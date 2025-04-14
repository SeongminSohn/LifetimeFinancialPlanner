package com.app.lifetimefinancialplanner.domain.context;

import com.app.lifetimefinancialplanner.domain.entity.IncomeEvent;
import com.app.lifetimefinancialplanner.domain.entity.Investment;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class SimulationContext {
    private int currentYear;
    private double currentInflationRate;
    private double inflationFactor;
    private double cumulativeInflation;
    private double adjustedAfterTaxContributionLimit;
    private List<IncomeEvent> updatedIncomeEvents;
    private List<Investment> updatedInvestments;
    private List<Investment> investmentsPurchasingPrices;
    // Current Year Variables
    private BigDecimal curYearIncome = BigDecimal.ZERO;
    private BigDecimal curYearSS = BigDecimal.ZERO;
    private BigDecimal curYearGains = BigDecimal.ZERO;
    private BigDecimal curYearEarlyWithdrawals = BigDecimal.ZERO;
    private BigDecimal totalInvestments = BigDecimal.ZERO;
    private BigDecimal totalExpenses = BigDecimal.ZERO;
    private BigDecimal totalTax = BigDecimal.ZERO;
    private BigDecimal cashBalance = BigDecimal.ZERO;
    // Previous Year Variables
    private BigDecimal prevYearIncome = BigDecimal.ZERO;
    private BigDecimal prevYearSS = BigDecimal.ZERO;
    private BigDecimal prevYearGains = BigDecimal.ZERO;
    private BigDecimal prevYearEarlyWithdrawals = BigDecimal.ZERO;
    private BigDecimal prevTotalExpenses = BigDecimal.ZERO;
    private BigDecimal prevTotalTax = BigDecimal.ZERO;
    private BigDecimal prevCashBalance = BigDecimal.ZERO;
    private String details;
    private LocalDateTime timestamp;
}
