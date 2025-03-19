package com.app.lifetimefinancialplanner.domain.entity;

import lombok.Getter;
import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity @Table(name = "TBL_STRATEGY")
@SequenceGenerator(name = "SEQ_STRATEGY_GENERATOR", sequenceName = "SEQ_STRATEGY", allocationSize = 1)
@Getter @ToString @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Strategy {
    @Id
    @GeneratedValue(generator = "SEQ_STRATEGY_GENERATOR")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SCENARIO_ID", nullable = false)
    private Scenario scenario;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(name = "STRATEGY_TYPE", length = 50, nullable = false)
    private String strategyType; // 'SPENDING', 'EXPENSE_WITHDRAWAL', 'ROTH_OPTIMIZER', 'RMD', 'ROTH_CONVERSION'

    @Column(name = "START_YEAR")
    private Integer startYear;

    @Column(name = "END_YEAR")
    private Integer endYear;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
