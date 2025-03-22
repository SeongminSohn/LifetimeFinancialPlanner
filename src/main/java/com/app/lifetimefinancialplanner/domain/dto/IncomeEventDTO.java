package com.app.lifetimefinancialplanner.domain.dto;

import lombok.Data;

@Data
public class IncomeEventDTO {
    private Long eventSeriesId;
    private Double initialAmount;
    private String annualChange;            // 'Fixed' or 'Distribution'
    private String inflationAdjustment;     // 'Y' or 'N'
    private String isSocialSecurity;        // 'Y' or 'N'
    private Double userPercentage;
    private Double spousePercentage;
}
