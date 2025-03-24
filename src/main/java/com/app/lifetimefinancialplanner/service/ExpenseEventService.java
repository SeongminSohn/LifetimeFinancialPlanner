package com.app.lifetimefinancialplanner.service;

import com.app.lifetimefinancialplanner.domain.dto.ExpenseEventDTO;
import com.app.lifetimefinancialplanner.domain.entity.ExpenseEvent;

public interface ExpenseEventService {
    ExpenseEvent createExpenseEvent(ExpenseEventDTO expenseEventDTO);
    ExpenseEvent getExpenseEvent(Long eventSeriesId);
}
