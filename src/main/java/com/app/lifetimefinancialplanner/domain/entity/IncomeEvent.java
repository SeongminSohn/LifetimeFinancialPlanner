package com.app.lifetimefinancialplanner.domain.entity;

import com.app.lifetimefinancialplanner.domain.embeddable.DistributionEmbeddable;
import lombok.*;
import javax.persistence.*;

@Entity @Table(name = "TBL_INCOME_EVENT")
@Getter @ToString @NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE) @Builder(toBuilder = true)
public class IncomeEvent {
    @Id
    @Column(name = "EVENT_SERIES_ID")
    private Long eventSeriesId;

    @Column(name = "INITIAL_AMOUNT", nullable = false)
    private Double initialAmount;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amountOrPercent", column = @Column(name = "ANNUAL_CHAGE_AMOUNT_OR_PERCENT", nullable = false)),
            @AttributeOverride(name = "distributionType", column = @Column(name = "ANNUAL_CHAGE_DISTRIBUTION_TYPE", nullable = false)),
            @AttributeOverride(name = "value", column = @Column(name = "ANNUAL_CHAGE_VALUE", nullable = false)),
            @AttributeOverride(name = "lower", column = @Column(name = "ANNUAL_CHAGE_LOWER")),
            @AttributeOverride(name = "upper", column = @Column(name = "ANNUAL_CHAGE_UPPER")),
            @AttributeOverride(name = "mean", column = @Column(name = "ANNUAL_CHAGE_MEAN")),
            @AttributeOverride(name = "stDev", column = @Column(name = "ANNUAL_CHAGE_STDDEV"))
    })
    private DistributionEmbeddable annualChange;

    @Column(name = "INFLATION_ADJUSTMENT", length = 1, nullable = false)
    private String inflationAdjustment; // 'Y' or 'N'

    @Column(name = "USER_PERCENTAGE", nullable = false)
    private Double userPercentage; // % <= 1

    @Column(name = "IS_SOCIAL_SECURITY", length = 1, nullable = false)
    private String isSocialSecurity; // 'Y' or 'N'

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
