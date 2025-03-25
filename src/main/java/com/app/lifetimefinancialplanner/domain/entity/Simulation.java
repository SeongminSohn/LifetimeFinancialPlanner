package com.app.lifetimefinancialplanner.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity @Table(name = "TBL_SIMULATION")
@SequenceGenerator(name = "SEQ_SIMULATION_GENERATOR", sequenceName = "SEQ_SIMULATION", allocationSize = 1)
@Getter @ToString @NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE) @Builder(toBuilder = true)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
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
