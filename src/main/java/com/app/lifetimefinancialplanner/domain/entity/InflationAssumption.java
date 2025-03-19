package com.app.lifetimefinancialplanner.domain.entity;

import lombok.Getter;
import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import javax.persistence.*;

@Entity @Table(name = "TBL_INFLATION_ASSUMPTION")
@SequenceGenerator(name = "SEQ_INFLATION_ASSUMPTION_GENERATOR", sequenceName = "SEQ_INFLATION_ASSUMPTION", allocationSize = 1)
@Getter @ToString @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InflationAssumption {
    @Id
    @GeneratedValue(generator = "SEQ_INFLATION_ASSUMPTION_GENERATOR")
    private Long id;

    @Column(nullable = false)
    private Double rate;
}
