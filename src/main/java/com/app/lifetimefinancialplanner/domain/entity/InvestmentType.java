package com.app.lifetimefinancialplanner.domain.entity;

import lombok.Getter;
import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity @Table(name = "TBL_INVESTMENT_TYPE")
@SequenceGenerator(name = "SEQ_INVESTMENT_TYPE_GENERATOR", sequenceName = "SEQ_INVESTMENT_TYPE", allocationSize = 1)
@Getter @ToString @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InvestmentType {
    @Id
    @GeneratedValue(generator = "SEQ_INVESTMENT_TYPE_GENERATOR")
    private Long id;

    @Column(nullable = false, length = 100)
    private String name; // 'Cash', 'S&P 500' or 'Municipal bonds'

    @Column(length = 500)
    private String description;

    @Column(name = "EXPECTED_ANNUAL_RETURN", length = 50, nullable = false)
    private String expectedAnnualReturn; // 'Fixed' or 'Distribution'

    @Column(name = "EXPENSE_RATIO", nullable = false)
    private Double expenseRatio;

    @Column(name = "EXPECTED_ANNUAL_INCOME", length = 50, nullable = false)
    private String expectedAnnualIncome; // 'Fixed' or 'Distribution'

    @Column(name = "TAXABILITY", length = 20, nullable = false)
    private String taxability; // 'Taxable' or 'TaxExempt'

    @CreationTimestamp
    @Column(name = "ins_date")
    private LocalDateTime createdAt = LocalDateTime.now();
}
