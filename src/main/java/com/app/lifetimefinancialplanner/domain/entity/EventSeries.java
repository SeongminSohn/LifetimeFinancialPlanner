package com.app.lifetimefinancialplanner.domain.entity;

import lombok.Getter;
import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import javax.persistence.*;

@Entity @Table(name = "TBL_EVENT_SERIES")
@SequenceGenerator(name = "SEQ_EVENT_SERIES_GENERATOR", sequenceName = "SEQ_EVENT_SERIES", allocationSize = 1)
@Getter @ToString @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventSeries {
    @Id
    @GeneratedValue(generator = "SEQ_EVENT_SERIES_GENERATOR")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SCENARIO_ID", nullable = false)
    private Scenario scenario;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(name = "START_YEAR", nullable = false)
    private Integer startYear;

    @Column(nullable = false)
    private Integer duration;

    @Column(name = "EVENT_TYPE", length = 20, nullable = false)
    private String eventType; // 'Income', 'Expense', 'Invest'
}
