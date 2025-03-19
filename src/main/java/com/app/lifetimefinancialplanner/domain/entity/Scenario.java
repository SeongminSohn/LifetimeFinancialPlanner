package com.app.lifetimefinancialplanner.domain.entity;

import lombok.Getter;
import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity @Table(name = "TBL_SCENARIO")
@SequenceGenerator(name = "SEQ_SCENARIO_GENERATOR", sequenceName = "SEQ_SCENARIO", allocationSize = 1)
@Getter @ToString @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Scenario {
    @Id
    @GeneratedValue(generator = "SEQ_SCENARIO_GENERATOR")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "IS_MARRIED", nullable = false, length = 1)
    private String isMarried; // 'Y' or 'N'

    @Column(name = "BIRTH_YEAR_USER", nullable = false)
    private Integer birthYearUser;

    @Column(name = "BIRTH_YEAR_SPOUSE")
    private Integer birthYearSpouse;

    @Column(name = "LIFE_EXPECTANCY_USER", nullable = false)
    private Integer lifeExpectancyUser;

    @Column(name = "LIFE_EXPECTANCY_SPOUSE")
    private Integer lifeExpectancySpouse;

    @Column(name = "FINANCIAL_GOAL", nullable = false)
    private Double financialGoal;

    @Column(name = "PRE_TAX_CONTRIBUTION_LIMIT", nullable = false)
    private Double preTaxContributionLimit;

    @Column(name = "AFTER_TAX_CONTRIBUTION_LIMIT", nullable = false)
    private Double afterTaxContributionLimit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FEDERAL_TAX_INFO_ID", nullable = false)
    private FederalTaxInfo federalTaxInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STATE_TAX_INFO_ID", nullable = false)
    private StateTaxInfo stateTaxInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INFLATION_ASSUMPTION_ID", nullable = false)
    private InflationAssumption inflationAssumption;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "STATE_OF_RESIDENCE", nullable = false, length = 2)
    private String stateOfResidence;
}
