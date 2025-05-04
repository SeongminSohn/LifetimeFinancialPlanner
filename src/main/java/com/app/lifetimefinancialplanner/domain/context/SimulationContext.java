package com.app.lifetimefinancialplanner.domain.context;

import com.app.lifetimefinancialplanner.domain.embeddable.AllocationEmbeddable;
import com.app.lifetimefinancialplanner.domain.embeddable.ExpenseEmbeddable;
import com.app.lifetimefinancialplanner.domain.entity.IncomeEvent;
import com.app.lifetimefinancialplanner.domain.entity.Investment;
import com.app.lifetimefinancialplanner.domain.entity.InvestEvent;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class SimulationContext {
    private int currentYear;
    private double currentInflationRate;
    private double inflationFactor;
    private double cumulativeInflation;
    private double adjustedAfterTaxContributionLimit;
    private List<IncomeEvent> updatedIncomeEvents = new ArrayList<>();
    private List<Investment> updatedInvestments = new ArrayList<>();
    private List<InvestEvent> updatedInvestEvents = new ArrayList<>();
    private List<Investment> investmentsPurchasingPrices = new ArrayList<>();
    // Current Year Variables
    private BigDecimal curYearIncome = BigDecimal.ZERO;
    private BigDecimal curYearSS = BigDecimal.ZERO;
    private BigDecimal curYearGains = BigDecimal.ZERO;
    private BigDecimal curYearEarlyWithdrawals = BigDecimal.ZERO;
    private BigDecimal totalInvestments = BigDecimal.ZERO;
    private BigDecimal totalExpenses = BigDecimal.ZERO;
    private BigDecimal totalTax = BigDecimal.ZERO;
    private BigDecimal cashBalance = BigDecimal.ZERO;


    // Save AssetAllocation for specification for Breakdown of Investments
    private List<AllocationEmbeddable> assetAllocations = new ArrayList<>();
    // Save Expense Results for specification for Breakdown of Expenses
    private List<ExpenseEmbeddable> expenseBreakdowns = new ArrayList<>();
    // Save Tax Results for specification for Breakdown of Taxes
    private BigDecimal federalTax = BigDecimal.ZERO;
    private BigDecimal stateTax = BigDecimal.ZERO;
    private BigDecimal capitalGainsTax = BigDecimal.ZERO;
    private BigDecimal earlyWithdrawalTax = BigDecimal.ZERO;


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
