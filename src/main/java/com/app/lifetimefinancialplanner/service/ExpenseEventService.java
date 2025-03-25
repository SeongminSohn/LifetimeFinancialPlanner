package com.app.lifetimefinancialplanner.service;

import com.app.lifetimefinancialplanner.domain.dto.ExpenseEventDTO;
import com.app.lifetimefinancialplanner.domain.entity.ExpenseEvent;

import java.util.List;

public interface ExpenseEventService {
    ExpenseEvent createExpenseEvent(ExpenseEventDTO expenseEventDTO);
    ExpenseEvent getExpenseEvent(Long eventSeriesId);
    ExpenseEvent updateExpenseEvent(Long eventSeriesId, ExpenseEventDTO expenseEventDTO);
    void deleteExpenseEvent(Long eventSeriesId);
    List<ExpenseEventDTO> getExpenseEventsBySeriesId(Long seriesId);
}
