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
public class SpendingStrategyId implements Serializable {

    @Column(name = "STRATEGY_ID")
    private Long strategyId;

    @Column(name = "EXPENSE_EVENT_ID")
    private Long expenseEventId;

    public SpendingStrategyId(Long strategyId, Long expenseEventId) {
        this.strategyId = strategyId;
        this.expenseEventId = expenseEventId;
    }
}
