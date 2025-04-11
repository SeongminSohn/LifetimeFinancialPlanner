package com.app.lifetimefinancialplanner.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity @Table(name = "TBL_SIMULATION_YEAR")
@SequenceGenerator(name = "SEQ_SIMULATION_YEAR_GENERATOR", sequenceName = "SEQ_SIMULATION_YEAR", allocationSize = 1)
@Getter @ToString @NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE) @Builder(toBuilder = true)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class SimulationYear {
    @Id
    @GeneratedValue(generator = "SEQ_SIMULATION_YEAR_GENERATOR")
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SIMULATION_ID", nullable = false)
    private Simulation simulation;

    @Column(name = "SIMULATION_INDEX", nullable = false)
    private Integer simulationIndex;

    @Column(name = "YEAR", nullable = false)
    private Integer year;

    @Column(name = "TOTAL_INVESTMENTS", nullable = false)
    private BigDecimal totalInvestments;

    @Column(name = "TOTAL_INCOME", nullable = false)
    private BigDecimal totalIncome;

    @Column(name = "TOTAL_EXPENSES", nullable = false)
    private BigDecimal totalExpenses;

    @Column(name = "TOTAL_TAX", nullable = false)
    private BigDecimal totalTax;

    @Column(name = "CASH_BALANCE", nullable = false)
    private BigDecimal cashBalance;

    @Column(name = "DETAILS", columnDefinition = "CLOB")
    private String details;

    @CreationTimestamp
    @Column(name = "INS_DATE", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
