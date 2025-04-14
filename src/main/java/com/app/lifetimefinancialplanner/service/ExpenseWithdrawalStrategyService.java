package com.app.lifetimefinancialplanner.service;

import com.app.lifetimefinancialplanner.domain.context.SimulationContext;
import com.app.lifetimefinancialplanner.domain.dto.ExpenseWithdrawalStrategyDTO;
import com.app.lifetimefinancialplanner.domain.entity.ExpenseWithdrawalStrategy;
import com.app.lifetimefinancialplanner.domain.entity.Scenario;

import java.math.BigDecimal;

public interface ExpenseWithdrawalStrategyService {
    ExpenseWithdrawalStrategy createExpenseWithdrawalStrategy(ExpenseWithdrawalStrategyDTO dto);
    ExpenseWithdrawalStrategy getExpenseWithdrawalStrategy(Long id);
    ExpenseWithdrawalStrategy updateExpenseWithdrawalStrategy(Long id, ExpenseWithdrawalStrategyDTO dto);
    void deleteExpenseWithdrawalStrategy(Long id);
    ExpenseWithdrawalStrategy getExpenseWithdrawalStrategyByScenarioId(Long scenarioId);
    void withdrawFundsForExpenses(Scenario scenario, SimulationContext context, BigDecimal withdrawalNeeded);
}
