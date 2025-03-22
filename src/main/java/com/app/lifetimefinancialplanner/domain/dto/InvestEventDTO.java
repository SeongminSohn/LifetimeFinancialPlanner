package com.app.lifetimefinancialplanner.domain.dto;

import lombok.Data;

@Data
public class InvestEventDTO {
    private Long eventSeriesId;
    private Double maxCash;
    private Long assetAllocationId;
}
