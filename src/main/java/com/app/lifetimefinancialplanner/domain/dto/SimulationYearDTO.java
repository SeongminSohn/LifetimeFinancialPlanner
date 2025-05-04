package com.app.lifetimefinancialplanner.domain.dto;

import com.app.lifetimefinancialplanner.domain.embeddable.AllocationEmbeddable;
import com.app.lifetimefinancialplanner.domain.embeddable.ExpenseEmbeddable;
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
    private BigDecimal curYearIncome;
    private BigDecimal curYearSS;
    private List<ExpenseEmbeddable> expenseBreakdowns;
    private BigDecimal federalTax;
    private BigDecimal stateTax;
    private BigDecimal capitalGainsTax;
    private BigDecimal earlyWithdrawalTax;

    private String details;
    private LocalDateTime createdAt;
}
