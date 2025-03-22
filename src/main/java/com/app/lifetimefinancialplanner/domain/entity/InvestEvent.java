package com.app.lifetimefinancialplanner.domain.entity;

import lombok.*;
import javax.persistence.*;

@Entity @Table(name = "TBL_INVEST_EVENT")
@Getter @ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE) @Builder(toBuilder = true)
public class InvestEvent {
    @Id
    @Column(name = "EVENT_SERIES_ID")
    private Long eventSeriesId;

    @Column(name = "MAX_CASH", nullable = false)
    private Double maxCash;

    @Column(name = "ASSET_ALLOCATION_ID", nullable = false)
    private Long assetAllocationId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "EVENT_SERIES_ID")
    private EventSeries eventSeries;
}
