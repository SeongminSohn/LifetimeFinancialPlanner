package com.app.lifetimefinancialplanner.domain.embeddable;

import lombok.Data;
import javax.persistence.Embeddable;

@Data
@Embeddable
public class DistributionEmbeddable {
    // "AMOUNT" or "PERCENT"
    private String amountOrPercent;

    // "FIXED", "UNIFORM", "NORMAL"
    private String distributionType;

    // For FIXED type: the fixed value.
    private Double value;

    // For UNIFORM type: the lower and upper bounds.
    private Double lower;
    private Double upper;

    // For NORMAL type: the mean and standard deviation.
    private Double mean;
    private Double stDev;
}
