package com.app.lifetimefinancialplanner.domain.entity;

import com.app.lifetimefinancialplanner.domain.compositePk.ExpenseWithdrawalStrategyId;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

import javax.persistence.*;

@Entity @Table(name = "TBL_EXPENSE_WITHDRAWAL_STRATEGY")
@Data @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExpenseWithdrawalStrategy {

    @EmbeddedId
    private ExpenseWithdrawalStrategyId id;

    @Column(length = 255)
    private String details;
}
