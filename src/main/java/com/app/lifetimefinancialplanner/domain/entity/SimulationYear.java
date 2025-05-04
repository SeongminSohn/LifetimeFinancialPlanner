package com.app.lifetimefinancialplanner.domain.entity;

import com.app.lifetimefinancialplanner.domain.embeddable.AllocationEmbeddable;
import com.app.lifetimefinancialplanner.domain.embeddable.ExpenseEmbeddable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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

    @ElementCollection
    @CollectionTable(
            name = "SIMULATION_YEAR_ASSET_ALLOCATION",
            joinColumns = @JoinColumn(name = "SIMULATION_ID")
    )
    private List<AllocationEmbeddable> assetAllocations;

    @Column(name = "CUR_YEAR_INCOME", nullable = false)
    private BigDecimal curYearIncome;

    @Column(name = "CUR_YEAR_SOCIAL_SECURITY", nullable = false)
    private BigDecimal curYearSS;

    @ElementCollection
    @CollectionTable(
            name = "SIMULATION_YEAR_EXPENSE_BREAKDOWN",
            joinColumns = @JoinColumn(name = "SIM_YEAR_ID")
    )
    private List<ExpenseEmbeddable> expenseBreakdowns;

    @Column(name="FEDERAL_TAX")
    private BigDecimal federalTax;

    @Column(name="STATE_TAX")
    private BigDecimal stateTax;

    @Column(name="CAPITAL_GAINS_TAX")
    private BigDecimal capitalGainsTax;

    @Column(name="EARLY_WITHDRAWAL_TAX")
    private BigDecimal earlyWithdrawalTax;

    @Column(name = "DETAILS", columnDefinition = "CLOB")
    private String details;

    @CreationTimestamp
    @Column(name = "INS_DATE", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
