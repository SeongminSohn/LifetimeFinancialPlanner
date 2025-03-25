package com.app.lifetimefinancialplanner.domain.entity;

import com.app.lifetimefinancialplanner.domain.embeddable.DistributionEmbeddable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import javax.persistence.*;

@Entity @Table(name = "TBL_INVEST_EVENT")
@Getter @ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE) @Builder(toBuilder = true)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class InvestEvent {
    @Id
    @Column(name = "EVENT_SERIES_ID")
    private Long eventSeriesId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amountOrPercent", column = @Column(name = "ASSET_ALLOCATION_AMOUNT_OR_PERCENT", nullable = false)),
            @AttributeOverride(name = "distributionType", column = @Column(name = "ASSET_ALLOCATION_DISTRIBUTION_TYPE", nullable = false)),
            @AttributeOverride(name = "value", column = @Column(name = "ASSET_ALLOCATION_VALUE")),
            @AttributeOverride(name = "lower", column = @Column(name = "ASSET_ALLOCATION_LOWER")),
            @AttributeOverride(name = "upper", column = @Column(name = "ASSET_ALLOCATION_UPPER")),
            @AttributeOverride(name = "mean", column = @Column(name = "ASSET_ALLOCATION_MEAN")),
            @AttributeOverride(name = "stDev", column = @Column(name = "ASSET_ALLOCATION_STDDEV"))
    })
    private DistributionEmbeddable assetAllocation;

    @Column(name = "MAX_CASH", nullable = false)
    private Double maxCash;

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
