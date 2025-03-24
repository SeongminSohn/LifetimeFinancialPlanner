package com.app.lifetimefinancialplanner.service;

import com.app.lifetimefinancialplanner.domain.dto.InvestmentTypeDTO;
import com.app.lifetimefinancialplanner.domain.entity.InvestmentType;

import java.util.List;
import java.util.Optional;

public interface InvestmentTypeService {
    InvestmentType createInvestmentType(InvestmentTypeDTO dto);
    Optional<InvestmentType> getInvestmentType(Long id);
    InvestmentType updateInvestmentType(Long id, InvestmentTypeDTO dto);
    void deleteInvestmentType(Long id);
    List<InvestmentTypeDTO> getInvestmentTypeList(Long scenarioId);
}
