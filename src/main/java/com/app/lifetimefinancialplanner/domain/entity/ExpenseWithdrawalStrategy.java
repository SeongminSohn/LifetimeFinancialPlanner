package com.app.lifetimefinancialplanner.domain.entity;

import com.app.lifetimefinancialplanner.domain.compositePk.ExpenseWithdrawalStrategyId;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;

@Entity @Table(name = "TBL_EXPENSE_WITHDRAWAL_STRATEGY")
@Getter @ToString @NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE) @Builder(toBuilder = true)
public class ExpenseWithdrawalStrategy {

    @EmbeddedId
    private ExpenseWithdrawalStrategyId id;

    @Column(length = 255)
    private String details;
}
