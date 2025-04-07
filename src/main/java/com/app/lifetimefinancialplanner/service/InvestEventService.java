package com.app.lifetimefinancialplanner.service;

import com.app.lifetimefinancialplanner.domain.dto.InvestEventDTO;
import com.app.lifetimefinancialplanner.domain.entity.InvestEvent;
import java.util.List;
import java.util.Optional;

public interface InvestEventService {
    InvestEvent createInvestEvent(InvestEventDTO dto);
    Optional<InvestEvent> getInvestEvent(Long eventSeriesId);
    InvestEvent updateInvestEvent(Long eventSeriesId, InvestEventDTO dto);
    void deleteInvestEvent(Long eventSeriesId);
    List<InvestEventDTO> getInvestEventsByScenarioId(Long scenarioId);
}
