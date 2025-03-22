package com.app.lifetimefinancialplanner.service;

import com.app.lifetimefinancialplanner.domain.entity.IncomeEvent;
import com.app.lifetimefinancialplanner.domain.dto.IncomeEventDTO;

public interface IncomeEventService {
    IncomeEvent createIncomeEvent(IncomeEventDTO incomeEventDTO);
    IncomeEvent getIncomeEvent(Long eventSeriesId);
    // Additional methods such as update or delete can be added as needed.
}
