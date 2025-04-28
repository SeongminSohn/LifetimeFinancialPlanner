package com.app.lifetimefinancialplanner.domain.dto;

import lombok.Data;

import java.util.Map;

@Data
public class EventSeriesDTO {
    private String name;
    private DistributionDTO start;
    private DistributionDTO duration;
    private String type;  // "income" / "expense" / "invest"
    // income & expense
    private Double initialAmount;
    private DistributionDTO changeDistribution;
    private String changeAmtOrPct;
    private Boolean inflationAdjusted;
    private Double userFraction;
    // income
    private Boolean socialSecurity;
    // expense
    private Boolean discretionary;
    // invest
    private Map<String,Double> assetAllocation;
    private Double maxCash;
}

