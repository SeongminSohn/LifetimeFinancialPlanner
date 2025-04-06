package com.app.lifetimefinancialplanner.service;

import com.app.lifetimefinancialplanner.domain.dto.ExpenseWithdrawalStrategyDTO;
import com.app.lifetimefinancialplanner.domain.entity.ExpenseWithdrawalStrategy;

import java.util.List;

public interface ExpenseWithdrawalStrategyService {
    ExpenseWithdrawalStrategy createExpenseWithdrawalStrategy(ExpenseWithdrawalStrategyDTO dto);
    ExpenseWithdrawalStrategy getExpenseWithdrawalStrategy(Long id);
    ExpenseWithdrawalStrategy updateExpenseWithdrawalStrategy(Long id, ExpenseWithdrawalStrategyDTO dto);
    void deleteExpenseWithdrawalStrategy(Long id);
//    List<ExpenseWithdrawalStrategy> getExpenseWithdrawalStrategiesByScenarioId(Long scenarioId);
}
