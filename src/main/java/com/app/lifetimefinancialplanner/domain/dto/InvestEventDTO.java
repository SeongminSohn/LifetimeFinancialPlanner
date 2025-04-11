package com.app.lifetimefinancialplanner.domain.dto;

import lombok.Data;
import java.util.List;

@Data
public class InvestEventDTO {
    private Long scenarioId;
    private Long eventSeriesId;
    private String name;
    private DistributionDTO startYear;
    private DistributionDTO duration;
    private String eventType;                       // 'INCOME', 'EXPENSE', 'INVEST'
    private List<AllocationDTO> assetAllocations;
    private Double maxCash;
}
