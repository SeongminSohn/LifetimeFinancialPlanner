package com.app.lifetimefinancialplanner.domain.dto;

import lombok.Data;

@Data
public class ExpenseEventDTO {
    private Long scenarioId;
    private Long eventSeriesId;
    private String name;
    private DistributionDTO startYear;
    private DistributionDTO duration;
    private String eventType;
    private Double initialAmount;
    private DistributionDTO annualChange;
    private String inflationAdjustment;     // 'Y' or 'N'
    private Double userPercentage;          // % <= 1.0
    private String isDiscretionary;         // 'Y' or 'N'
}
