package com.app.lifetimefinancialplanner.domain.entity;

import com.app.lifetimefinancialplanner.domain.compositePk.SpendingStrategyId;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

import javax.persistence.*;

@Entity @Table(name = "TBL_SPENDING_STRATEGY")
@Data @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SpendingStrategy {
    @EmbeddedId
    private SpendingStrategyId id;

    @Column(length = 255)
    private String details;
}
