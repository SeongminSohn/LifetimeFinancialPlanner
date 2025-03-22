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

    @Column(name = "ANNUAL_CHANGE", length = 50, nullable = false)
    private String annualChange; // 'Fixed' or 'Distribution'

    @Column(name = "INFLATION_ADJUSTMENT", nullable = false, length = 1)
    private String inflationAdjustment; // 'Y' or 'N'

    @Column(name = "IS_SOCIAL_SECURITY", nullable = false, length = 1)
    private String isSocialSecurity; // 'Y' or 'N'

    @Column(name = "USER_PERCENTAGE", nullable = false)
    private Double userPercentage;

    @Column(name = "SPOUSE_PERCENTAGE")
    private Double spousePercentage;

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
