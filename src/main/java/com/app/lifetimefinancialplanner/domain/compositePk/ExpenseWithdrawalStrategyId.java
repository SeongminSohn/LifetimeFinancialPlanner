package com.app.lifetimefinancialplanner.domain.compositePk;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor
public class ExpenseWithdrawalStrategyId implements Serializable {

    @Column(name = "STRATEGY_ID")
    private Long strategyId;

    @Column(name = "INVESTMENT_ID")
    private Long investmentId;

    public ExpenseWithdrawalStrategyId(Long strategyId, Long investmentId) {
        this.strategyId = strategyId;
        this.investmentId = investmentId;
    }
}
