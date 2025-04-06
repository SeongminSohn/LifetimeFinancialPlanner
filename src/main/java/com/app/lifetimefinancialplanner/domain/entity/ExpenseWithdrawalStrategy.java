package com.app.lifetimefinancialplanner.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity @Table(name = "TBL_EXPENSE_WITHDRAWAL_STRATEGY")
@Getter @ToString @NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE) @Builder(toBuilder = true)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ExpenseWithdrawalStrategy {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_EXPENSE_WITHDRAWAL_STRATEGY")
    @SequenceGenerator(name = "SEQ_EXPENSE_WITHDRAWAL_STRATEGY", sequenceName = "SEQ_EXPENSE_WITHDRAWAL_STRATEGY", allocationSize = 1)
    private Long id;

    @Column(name = "SCENARIO_ID", nullable = false)
    private Long scenarioId;

    // Field to store the ordered list of investment IDs as a JSON array string
    @Column(name = "SELLING_ORDER", columnDefinition = "TEXT", nullable = false)
    private String sellingOrder;

    @CreationTimestamp
    @Column(name = "ins_date")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}