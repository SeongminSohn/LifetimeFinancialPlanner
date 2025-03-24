package com.app.lifetimefinancialplanner.domain.entity;

import com.app.lifetimefinancialplanner.domain.embeddable.DistributionEmbeddable;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity @Table(name = "TBL_SCENARIO")
@SequenceGenerator(name = "SEQ_SCENARIO_GENERATOR", sequenceName = "SEQ_SCENARIO", allocationSize = 1)
@Getter @ToString @NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE) @Builder(toBuilder = true)
public class Scenario {
    @Id
    @GeneratedValue(generator = "SEQ_SCENARIO_GENERATOR")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "MARITAL_STATUS", nullable = false, length = 1)
    private String maritalStatus;

    @Column(name = "BIRTH_YEAR_USER", nullable = false)
    private Integer birthYearUser;

    @Column(name = "BIRTH_YEAR_SPOUSE")
    private Integer birthYearSpouse;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amountOrPercent", column = @Column(name = "LIFE_EXPECTANCY_USER_AMOUNT_OR_PERCENT", nullable = false)),
            @AttributeOverride(name = "distributionType", column = @Column(name = "LIFE_EXPECTANCY_USER_DISTRIBUTION_TYPE", nullable = false)),
            @AttributeOverride(name = "value", column = @Column(name = "LIFE_EXPECTANCY_USER_VALUE")),
            @AttributeOverride(name = "lower", column = @Column(name = "LIFE_EXPECTANCY_USER_LOWER")),
            @AttributeOverride(name = "upper", column = @Column(name = "LIFE_EXPECTANCY_USER_UPPER")),
            @AttributeOverride(name = "mean", column = @Column(name = "LIFE_EXPECTANCY_USER_MEAN")),
            @AttributeOverride(name = "stDev", column = @Column(name = "LIFE_EXPECTANCY_USER_STDDEV"))
    })
    private DistributionEmbeddable lifeExpectancyUser;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amountOrPercent", column = @Column(name = "LIFE_EXPECTANCY_SPOUSE_AMOUNT_OR_PERCENT", nullable = true)),
            @AttributeOverride(name = "distributionType", column = @Column(name = "LIFE_EXPECTANCY_SPOUSE_DISTRIBUTION_TYPE", nullable = true)),
            @AttributeOverride(name = "value", column = @Column(name = "LIFE_EXPECTANCY_SPOUSE_VALUE")),
            @AttributeOverride(name = "lower", column = @Column(name = "LIFE_EXPECTANCY_SPOUSE_LOWER")),
            @AttributeOverride(name = "upper", column = @Column(name = "LIFE_EXPECTANCY_SPOUSE_UPPER")),
            @AttributeOverride(name = "mean", column = @Column(name = "LIFE_EXPECTANCY_SPOUSE_MEAN")),
            @AttributeOverride(name = "stDev", column = @Column(name = "LIFE_EXPECTANCY_SPOUSE_STDDEV"))
    })
    private DistributionEmbeddable lifeExpectancySpouse;

    @Column(name = "FINANCIAL_GOAL", nullable = false)
    private Double financialGoal;

    @Column(name = "PRE_TAX_CONTRIBUTION_LIMIT", nullable = false)
    private Double preTaxContributionLimit;

    @Column(name = "AFTER_TAX_CONTRIBUTION_LIMIT", nullable = false)
    private Double afterTaxContributionLimit;

    @Column(name = "STATE_OF_RESIDENCE", nullable = false, length = 2)
    private String stateOfResidence;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amountOrPercent", column = @Column(name = "INFLATION_AMOUNT_OR_PERCENT", nullable = false)),
            @AttributeOverride(name = "distributionType", column = @Column(name = "INFLATION_DISTRIBUTION_TYPE", nullable = false)),
            @AttributeOverride(name = "value", column = @Column(name = "INFLATION_VALUE")),
            @AttributeOverride(name = "lower", column = @Column(name = "INFLATION_LOWER")),
            @AttributeOverride(name = "upper", column = @Column(name = "INFLATION_UPPER")),
            @AttributeOverride(name = "mean", column = @Column(name = "INFLATION_MEAN")),
            @AttributeOverride(name = "stDev", column = @Column(name = "INFLATION_STDEV"))
    })
    private DistributionEmbeddable inflationAssumption;

    @CreationTimestamp
    @Column(name = "ins_date")
    private LocalDateTime createdAt = LocalDateTime.now();
}
