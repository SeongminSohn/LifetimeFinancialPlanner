package com.app.lifetimefinancialplanner.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity @Table(name = "TBL_INVESTMENT")
@SequenceGenerator(name = "SEQ_INVESTMENT_GENERATOR", sequenceName = "SEQ_INVESTMENT", allocationSize = 1)
@Getter @ToString @NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE) @Builder(toBuilder = true)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Investment {
    @Id
    @GeneratedValue(generator = "SEQ_INVESTMENT_GENERATOR")
    private Long id;

    @Column(nullable = false)
    private Double value;

    @Column(name = "TAX_STATUS", length = 20, nullable = false)
    private String taxStatus; // 'NonRetirement', 'PreTax', 'AfterTax'

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INVESTMENT_TYPE_ID", nullable = false)
    private InvestmentType investmentType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SCENARIO_ID", nullable = false)
    private Scenario scenario;

    @CreationTimestamp
    @Column(name = "ins_date")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
