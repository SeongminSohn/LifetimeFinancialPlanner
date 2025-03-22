package com.app.lifetimefinancialplanner.domain.entity;

import lombok.Getter;
import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity @Table(name = "TBL_SIMULATION")
@Getter @ToString @NoArgsConstructor(access = AccessLevel.PROTECTED)
@SequenceGenerator(name = "SEQ_SIMULATION_GENERATOR", sequenceName = "SEQ_SIMULATION", allocationSize = 1)
public class Simulation {
    @Id
    @GeneratedValue(generator = "SEQ_SIMULATION_GENERATOR")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SCENARIO_ID", nullable = false)
    private Scenario scenario;

    @Column(name = "SIMULATION_DATE", nullable = false)
    private LocalDate simulationDate;

    @Column(length = 100, nullable = false)
    private String result;
}
