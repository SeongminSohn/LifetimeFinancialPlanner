package com.app.lifetimefinancialplanner.domain.dto;

import lombok.Data;

@Data
public class ExpenseWithdrawalStrategyDTO {
    private Long id;
    private Long scenarioId;
    private String sellingOrder;
}
