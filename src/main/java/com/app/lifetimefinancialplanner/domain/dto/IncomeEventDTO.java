package com.app.lifetimefinancialplanner.domain.dto;

import lombok.Data;

@Data
public class IncomeEventDTO {
    private Long eventSeriesId;
    private String name;
    private Integer startYear;
    private Integer duration;
    private String eventType;
    private Double initialAmount;
    private DistributionDTO changeDistribution;
    private String inflationAdjustment;     // 'Y' or 'N'
    private String isSocialSecurity;        // 'Y' or 'N'
    private Double userPercentage;
}
