package com.app.lifetimefinancialplanner.domain.entity;

import lombok.Getter;
import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import javax.persistence.*;

@Entity @Table(name = "TBL_EXPENSE_EVENT")
@Getter @ToString @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExpenseEvent {
    @Id
    @Column(name = "EVENT_SERIES_ID")
    private Long eventSeriesId;

    @Column(name = "INITIAL_AMOUNT", nullable = false)
    private Double initialAmount;

    @Column(name = "ANNUAL_CHANGE", length = 50, nullable = false)
    private String annualChange; // 'Fixed' or 'Distribution'

    @Column(name = "INFLATION_ADJUSTMENT", nullable = false, length = 1)
    private String inflationAdjustment; // 'Y' or 'N'

    @Column(name = "USER_PERCENTAGE", nullable = false)
    private Double userPercentage;

    @Column(name = "SPOUSE_PERCENTAGE")
    private Double spousePercentage;

    @OneToOne
    @MapsId
    @JoinColumn(name = "EVENT_SERIES_ID")
    private EventSeries eventSeries;
}
