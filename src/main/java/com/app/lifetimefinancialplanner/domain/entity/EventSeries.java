package com.app.lifetimefinancialplanner.domain.entity;

import com.app.lifetimefinancialplanner.domain.embeddable.DistributionEmbeddable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import javax.persistence.*;

@Entity @Table(name = "TBL_EVENT_SERIES")
@SequenceGenerator(name = "SEQ_EVENT_SERIES_GENERATOR", sequenceName = "SEQ_EVENT_SERIES", allocationSize = 1)
@Getter @ToString @NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE) @Builder(toBuilder = true)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class EventSeries {
    @Id
    @GeneratedValue(generator = "SEQ_EVENT_SERIES_GENERATOR")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SCENARIO_ID", nullable = false)
    private Scenario scenario;

    @Column(nullable = false, length = 100)
    private String name;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amountOrPercent", column = @Column(name = "START_YEAR_AMOUNT_OR_PERCENT", nullable = false)),
            @AttributeOverride(name = "distributionType", column = @Column(name = "START_YEAR_DISTRIBUTION_TYPE", nullable = false)),
            @AttributeOverride(name = "value", column = @Column(name = "START_YEAR_VALUE")),
            @AttributeOverride(name = "lower", column = @Column(name = "START_YEAR_LOWER")),
            @AttributeOverride(name = "upper", column = @Column(name = "START_YEAR_UPPER")),
            @AttributeOverride(name = "mean", column = @Column(name = "START_YEAR_MEAN")),
            @AttributeOverride(name = "stDev", column = @Column(name = "START_YEAR_STDDEV"))
    })
    private DistributionEmbeddable startYear;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amountOrPercent", column = @Column(name = "DURATION_AMOUNT_OR_PERCENT", nullable = false)),
            @AttributeOverride(name = "distributionType", column = @Column(name = "DURATION_DISTRIBUTION_TYPE", nullable = false)),
            @AttributeOverride(name = "value", column = @Column(name = "DURATION_VALUE")),
            @AttributeOverride(name = "lower", column = @Column(name = "DURATION_LOWER")),
            @AttributeOverride(name = "upper", column = @Column(name = "DURATION_UPPER")),
            @AttributeOverride(name = "mean", column = @Column(name = "DURATION_MEAN")),
            @AttributeOverride(name = "stDev", column = @Column(name = "DURATION_STDDEV"))
    })
    private DistributionEmbeddable duration;

    @Column(name = "EVENT_TYPE", length = 10, nullable = false)
    private String eventType; // 'INCOME', 'EXPENSE', 'INVEST'
}
