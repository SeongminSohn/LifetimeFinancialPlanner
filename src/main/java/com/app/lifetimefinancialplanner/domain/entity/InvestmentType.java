package com.app.lifetimefinancialplanner.domain.entity;

import com.app.lifetimefinancialplanner.domain.embeddable.DistributionEmbeddable;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity @Table(name = "TBL_INVESTMENT_TYPE")
@SequenceGenerator(name = "SEQ_INVESTMENT_TYPE_GENERATOR", sequenceName = "SEQ_INVESTMENT_TYPE", allocationSize = 1)
@Getter @ToString @NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE) @Builder(toBuilder = true)
public class InvestmentType {
    @Id
    @GeneratedValue(generator = "SEQ_INVESTMENT_TYPE_GENERATOR")
    private Long id;

    @Column(length = 20, nullable = false)
    private String name; // 'Cash', 'S&P 500' or 'Municipal bonds'

    @Column(length = 500)
    private String description;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amountOrPercent", column = @Column(name = "EXPECTED_ANNUAL_RETURN_AMOUNT_OR_PERCENT", nullable = false)),
            @AttributeOverride(name = "distributionType", column = @Column(name = "EXPECTED_ANNUAL_RETURN_DISTRIBUTION_TYPE", nullable = false)),
            @AttributeOverride(name = "value", column = @Column(name = "EXPECTED_ANNUAL_RETURN_VALUE")),
            @AttributeOverride(name = "lower", column = @Column(name = "EXPECTED_ANNUAL_RETURN_LOWER")),
            @AttributeOverride(name = "upper", column = @Column(name = "EXPECTED_ANNUAL_RETURN_UPPER")),
            @AttributeOverride(name = "mean", column = @Column(name = "EXPECTED_ANNUAL_RETURN_MEAN")),
            @AttributeOverride(name = "stDev", column = @Column(name = "EXPECTED_ANNUAL_RETURN_STDDEV"))
    })
    private DistributionEmbeddable expectedAnnualReturn;

    @Column(name = "EXPENSE_RATIO", nullable = false)
    private Double expenseRatio;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amountOrPercent", column = @Column(name = "EXPECTED_ANNUAL_INCOME_AMOUNT_OR_PERCENT", nullable = false)),
            @AttributeOverride(name = "distributionType", column = @Column(name = "EXPECTED_ANNUAL_INCOME_DISTRIBUTION_TYPE", nullable = false)),
            @AttributeOverride(name = "value", column = @Column(name = "EXPECTED_ANNUAL_INCOME_VALUE")),
            @AttributeOverride(name = "lower", column = @Column(name = "EXPECTED_ANNUAL_INCOME_LOWER")),
            @AttributeOverride(name = "upper", column = @Column(name = "EXPECTED_ANNUAL_INCOME_UPPER")),
            @AttributeOverride(name = "mean", column = @Column(name = "EXPECTED_ANNUAL_INCOME_MEAN")),
            @AttributeOverride(name = "stDev", column = @Column(name = "EXPECTED_ANNUAL_INCOME_STDDEV"))
    })
    private DistributionEmbeddable expectedAnnualIncome;

    @Column(name = "TAXABILITY", length = 1, nullable = false)
    private String taxability; // 'Y' or 'N'

    @CreationTimestamp
    @Column(name = "ins_date")
    private LocalDateTime createdAt = LocalDateTime.now();
}
