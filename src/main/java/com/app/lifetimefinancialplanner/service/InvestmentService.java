package com.app.lifetimefinancialplanner.service;

import com.app.lifetimefinancialplanner.domain.dto.InvestmentDTO;
import com.app.lifetimefinancialplanner.domain.entity.Investment;

import java.util.List;
import java.util.Optional;

public interface InvestmentService {
    Investment createInvestment(InvestmentDTO dto);
    Optional<Investment> getInvestment(Long id);
    Investment updateInvestment(Long id, InvestmentDTO dto);
    void deleteInvestment(Long id);
    List<InvestmentDTO> getInvestmentListByScenarioId(Long scenarioId);
}
