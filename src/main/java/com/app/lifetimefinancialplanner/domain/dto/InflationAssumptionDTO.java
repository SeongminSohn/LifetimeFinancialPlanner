package com.app.lifetimefinancialplanner.domain.dto;

import lombok.Data;

@Data
public class InflationAssumptionDTO {
    private String distributionType;
    private Double fixedRate;
    private Double minValue;
    private Double maxValue;
    private Double mean;
    private Double stdDev;
}
