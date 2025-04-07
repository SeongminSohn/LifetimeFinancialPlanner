package com.app.lifetimefinancialplanner.domain.dto;

import lombok.Data;
import java.util.List;

@Data
public class ExpenseWithdrawalStrategyDTO {
    private Long id;
    private Long scenarioId;
    private List<String> sellingOrder;
}
