package com.app.lifetimefinancialplanner.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity @Table(name = "TBL_SIMULATION_YEAR")
@SequenceGenerator(name = "SEQ_SIMULATION_YEAR_GENERATOR", sequenceName = "SEQ_SIMULATION_YEAR", allocationSize = 1)
@Getter @ToString @NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE) @Builder(toBuilder = true)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class SimulationYear {
    @Id
    @GeneratedValue(generator = "SEQ_SIMULATION_YEAR_GENERATOR")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SIMULATION_ID", nullable = false)
    private Simulation simulation;

    // The simulated year
    @Column(name = "SIMULATED_YEAR", nullable = false)
    private Integer simulatedYear;

    // Total value of investments at the end of the year.
    @Column(name = "TOTAL_INVESTMENT", nullable = false)
    private Double totalInvestment;

    // Total income for the year.
    @Column(name = "TOTAL_INCOME", nullable = false)
    private Double totalIncome;

    // Total expenses for the year.
    @Column(name = "TOTAL_EXPENSE", nullable = false)
    private Double totalExpense;

    // Taxes paid in that year.
    @Column(name = "TAXES_PAID", nullable = false)
    private Double taxesPaid;

    // Cash balance at the end of the year.
    @Column(name = "CASH_BALANCE", nullable = false)
    private Double cashBalance;

    @CreationTimestamp
    @Column(name = "INS_DATE", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
