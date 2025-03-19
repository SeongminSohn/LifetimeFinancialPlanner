package com.app.lifetimefinancialplanner.domain.entity;

import lombok.Getter;
import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity @Table(name = "TBL_CHART")
@Getter @ToString @NoArgsConstructor(access = AccessLevel.PROTECTED)
@SequenceGenerator(name = "SEQ_CHART_GENERATOR", sequenceName = "SEQ_CHART", allocationSize = 1)
public class Chart {
    @Id
    @GeneratedValue(generator = "SEQ_CHART_GENERATOR")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SIMULATION_ID", nullable = false)
    private Simulation simulation;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(name = "CHART_TYPE", length = 50, nullable = false)
    private String chartType;

    @Column(columnDefinition = "CLOB", nullable = false)
    private String data;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
