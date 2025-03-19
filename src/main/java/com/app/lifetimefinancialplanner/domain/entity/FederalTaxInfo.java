package com.app.lifetimefinancialplanner.domain.entity;

import lombok.Getter;
import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity @Table(name = "TBL_FEDERAL_TAX_INFO")
@SequenceGenerator(name = "SEQ_FEDERAL_TAX_INFO_GENERATOR", sequenceName = "SEQ_FEDERAL_TAX_INFO", allocationSize = 1)
@Getter @ToString @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FederalTaxInfo {
    @Id
    @GeneratedValue(generator = "SEQ_FEDERAL_TAX_INFO_GENERATOR")
    private Long id;

    @Column(name = "INCOME_TAX_RATES", columnDefinition = "CLOB", nullable = false)
    private String incomeTaxRates;

    @Column(name = "STANDARD_DEDUCTIONS", columnDefinition = "CLOB", nullable = false)
    private String standardDeductions;

    @Column(name = "CAPITAL_GAINS_TAX_RATES", columnDefinition = "CLOB", nullable = false)
    private String capitalGainsTaxRates;

    @Column(name = "SOCIAL_SECURITY_TAXABLE_PERCENTAGES", columnDefinition = "CLOB", nullable = false)
    private String socialSecurityTaxablePercentages;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
