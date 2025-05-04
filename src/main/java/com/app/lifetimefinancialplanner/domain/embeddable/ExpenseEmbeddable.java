package com.app.lifetimefinancialplanner.domain.embeddable;

import lombok.Data;

import javax.persistence.Embeddable;
import java.math.BigDecimal;

@Data
@Embeddable
public class ExpenseEmbeddable {
    private Long eventSeriesId;
    private BigDecimal amount;
}
