package com.app.lifetimefinancialplanner.domain.dto;

import lombok.Data;

@Data
public class InvestmentTypeDTO {
    private Long id;
    private Long scenarioId;
    private String name; // 'Cash', 'S&P 500' or 'Municipal bonds'
    private String description;
    private DistributionDTO expectedAnnualReturn;
    private Double expenseRatio;
    private DistributionDTO expectedAnnualIncome;
    private String taxability; // 'Y' or 'N'
}
