package com.app.lifetimefinancialplanner.domain.embeddable;

import lombok.Data;

import javax.persistence.Embeddable;

@Data
@Embeddable
public class AllocationEmbeddable {
    // Investment id (InvestmentType + TaxStatus)
    private String investmentKey;

    // allocation ratio (e.g 0.6)
    private Double ratio;
}
