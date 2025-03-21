package com.app.lifetimefinancialplanner.domain.entity;

import lombok.Getter;
import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity @Table(name = "TBL_STATE_TAX_INFO")
@SequenceGenerator(name = "SEQ_STATE_TAX_INFO_GENERATOR", sequenceName = "SEQ_STATE_TAX_INFO", allocationSize = 1)
@Getter @ToString @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StateTaxInfo {
    @Id
    @GeneratedValue(generator = "SEQ_STATE_TAX_INFO_GENERATOR")
    private Long id;

    @Column(name = "STATE_CODE", length = 2, nullable = false)
    private String stateCode;

    @Column(name = "STATE_TAX_RATES", columnDefinition = "CLOB", nullable = false)
    private String stateTaxRates;

    @CreationTimestamp
    @Column(name = "ins_date")
    private LocalDateTime createdAt = LocalDateTime.now();
}
