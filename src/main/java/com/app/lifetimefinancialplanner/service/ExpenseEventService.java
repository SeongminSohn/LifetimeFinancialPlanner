package com.app.lifetimefinancialplanner.service;

import com.app.lifetimefinancialplanner.domain.context.SimulationContext;
import com.app.lifetimefinancialplanner.domain.dto.ExpenseEventDTO;
import com.app.lifetimefinancialplanner.domain.entity.ExpenseEvent;
import com.app.lifetimefinancialplanner.domain.entity.Scenario;

import java.math.BigDecimal;
import java.util.List;

public interface ExpenseEventService {
    ExpenseEvent createExpenseEvent(ExpenseEventDTO expenseEventDTO);
    ExpenseEvent getExpenseEvent(Long eventSeriesId);
    ExpenseEvent updateExpenseEvent(Long eventSeriesId, ExpenseEventDTO expenseEventDTO);
    void deleteExpenseEvent(Long eventSeriesId);
    List<ExpenseEventDTO> getExpenseEventsByScenarioId(Long seriesId);
    BigDecimal calculateNonDiscretionaryExpense(Scenario scenario, SimulationContext context);
}
