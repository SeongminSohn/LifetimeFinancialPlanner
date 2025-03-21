package com.app.lifetimefinancialplanner.domain.entity;

import lombok.*;
import javax.persistence.*;

@Entity @Table(name = "TBL_INFLATION_ASSUMPTION")
@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE) @Builder(toBuilder = true)
@SequenceGenerator(name = "SEQ_INFLATION_ASSUMPTION_GENERATOR", sequenceName = "SEQ_INFLATION_ASSUMPTION", allocationSize = 1)
public class InflationAssumption {
    @Id
    @GeneratedValue(generator = "SEQ_INFLATION_ASSUMPTION_GENERATOR")
    private Long id;

    // Distribution type: "FIXED", "UNIFORM", or "NORMAL"
    @Column(nullable = false, length = 30)
    private String distributionType;

    // When distributionType = "FIXED"
    private Double fixedRate;

    // When distributionType = "UNIFORM"
    private Double minValue;
    private Double maxValue;

    // When distributionType = "NORMAL"
    private Double mean;
    private Double stdDev;
}
