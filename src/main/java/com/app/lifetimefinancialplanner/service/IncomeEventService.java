package com.app.lifetimefinancialplanner.service;

import com.app.lifetimefinancialplanner.domain.context.SimulationContext;
import com.app.lifetimefinancialplanner.domain.entity.IncomeEvent;
import com.app.lifetimefinancialplanner.domain.dto.IncomeEventDTO;
import com.app.lifetimefinancialplanner.domain.dto.ScenarioDTO;
import com.app.lifetimefinancialplanner.domain.entity.Scenario;

import java.util.List;

public interface IncomeEventService {
    IncomeEvent createIncomeEvent(IncomeEventDTO incomeEventDTO);
    IncomeEvent getIncomeEvent(Long eventSeriesId);
    IncomeEvent updateIncomeEvent(Long eventSeriesId, IncomeEventDTO incomeEventDTO);
    void deleteIncomeEvent(Long eventSeriesId);
    List<IncomeEventDTO> getIncomeEventListByScenarioId(Long seriesId);
    void runIncomeEvents(Scenario scenario, SimulationContext context);
}
