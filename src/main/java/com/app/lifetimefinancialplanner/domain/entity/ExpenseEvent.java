package com.app.lifetimefinancialplanner.domain.entity;

import com.app.lifetimefinancialplanner.domain.embeddable.DistributionEmbeddable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;

@Entity @Table(name = "TBL_EXPENSE_EVENT")
@Getter @ToString @NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE) @Builder(toBuilder = true)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ExpenseEvent {
    @Id
    @Column(name = "EVENT_SERIES_ID")
    private Long eventSeriesId;

    @Column(name = "INITIAL_AMOUNT", nullable = false)
    private Double initialAmount;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amountOrPercent", column = @Column(name = "ANNUAL_CHANGE_AMOUNT_OR_PERCENT", nullable = false)),
            @AttributeOverride(name = "distributionType", column = @Column(name = "ANNUAL_CHANGE_DISTRIBUTION_TYPE", nullable = false)),
            @AttributeOverride(name = "value", column = @Column(name = "ANNUAL_CHANGE_VALUE")),
            @AttributeOverride(name = "lower", column = @Column(name = "ANNUAL_CHANGE_LOWER")),
            @AttributeOverride(name = "upper", column = @Column(name = "ANNUAL_CHANGE_UPPER")),
            @AttributeOverride(name = "mean", column = @Column(name = "ANNUAL_CHANGE_MEAN")),
            @AttributeOverride(name = "stDev", column = @Column(name = "ANNUAL_CHANGE_STDDEV"))
    })
    private DistributionEmbeddable annualChange;

    @Column(name = "INFLATION_ADJUSTMENT", length = 1, nullable = false)
    private String inflationAdjustment; // 'Y' or 'N'

    @Column(name = "USER_PERCENTAGE", nullable = false)
    private Double userPercentage; // % <= 1

    @Column(name = "IS_DISCRETIONARY", length = 1, nullable = false)
    private String isDiscretionary; // 'Y' or 'N'

    @OneToOne
    @MapsId
    @JoinColumn(name = "EVENT_SERIES_ID")
    private EventSeries eventSeries;

    @PrePersist
    private void onPrePersist() {
        if (this.eventSeries != null) {
            this.eventSeriesId = this.eventSeries.getId();
        }
    }
}
