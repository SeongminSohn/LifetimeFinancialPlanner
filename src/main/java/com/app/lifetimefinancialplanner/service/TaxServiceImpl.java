package com.app.lifetimefinancialplanner.service;

import com.app.lifetimefinancialplanner.config.TaxDataLoader;
import com.app.lifetimefinancialplanner.domain.dto.FederalTaxDTO;
import com.app.lifetimefinancialplanner.domain.dto.StateTaxDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TaxServiceImpl implements TaxService {

    private final FederalTaxDTO federalTaxDTO;
    private final StateTaxDTO stateTaxDTO;

    public TaxServiceImpl(TaxDataLoader taxDataLoader) {
        this.federalTaxDTO = taxDataLoader.getFederalTaxDTO();
        this.stateTaxDTO = taxDataLoader.getStateTaxDTO();
    }

    @Override
    public BigDecimal calculateFederalTax(double income, String filingStatus) {
        // Use federalTaxConfig data to calculate tax
        // e.g. retrieve brackets for filingStatus, compute progressive tax
        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal calculateStateTax(double income, String stateCode, String filingStatus) {
        // Use stateTaxConfig data to calculate tax
        return BigDecimal.ZERO;
    }
}
