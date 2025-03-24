package com.app.lifetimefinancialplanner.service;

import com.app.lifetimefinancialplanner.domain.dto.InvestmentDTO;
import com.app.lifetimefinancialplanner.domain.entity.Investment;

import java.util.Optional;

public interface InvestmentService {
    Investment createInvestmentType(InvestmentDTO dto);
    Optional<Investment> getInvestmentType(Long id);
    Investment updateInvestmentType(Long id, InvestmentDTO dto);
    void deleteInvestmentType(Long id);
}
