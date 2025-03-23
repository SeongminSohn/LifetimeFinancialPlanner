package com.app.lifetimefinancialplanner.domain.entity;

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

    @Column(name = "CHANGE_AMT_OR_PCT", length = 20, nullable = false)
    private String changeAmtOrPct; // "AMOUNT" or "PERCENT"

    @Column(name = "CHANGE_DISTRIBUTION", length = 20, nullable = false)
    private String changeDistribution; // "FIXED", "UNIFORM", "NORMAL"

    @Column(name = "INFLATION_ADJUSTMENT", nullable = false, length = 1)
    private String inflationAdjustment; // 'Y' or 'N'

    @Column(name = "IS_SOCIAL_SECURITY", nullable = false, length = 1)
    private String isSocialSecurity; // 'Y' or 'N'

    @Column(name = "USER_PERCENTAGE", nullable = false)
    private Double userPercentage; // % <= 1

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
