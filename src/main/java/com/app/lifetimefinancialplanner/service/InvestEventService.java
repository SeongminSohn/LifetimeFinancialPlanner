package com.app.lifetimefinancialplanner.service;

import com.app.lifetimefinancialplanner.domain.dto.InvestEventDTO;
import com.app.lifetimefinancialplanner.domain.entity.InvestEvent;

public interface InvestEventService {
    InvestEvent createInvestEvent(InvestEventDTO investEventDTO);
    InvestEvent getInvestEvent(Long eventSeriesId);
    InvestEvent updateInvestEvent(Long eventSeriesId, InvestEventDTO investEventDTO);
    void deleteInvestEvent(Long eventSeriesId);
}
