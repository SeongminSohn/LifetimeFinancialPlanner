package com.app.lifetimefinancialplanner.domain.dto;

import lombok.Data;

@Data
public class InvestEventDTO {
    private Long scenarioId;
    private Long eventSeriesId;
    private String name;
    private DistributionDTO startYear;
    private DistributionDTO duration;
    private String eventType;
    private DistributionDTO assetAllocation;
    private Double maxCash;
}
