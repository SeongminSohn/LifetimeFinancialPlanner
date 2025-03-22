package com.app.lifetimefinancialplanner.service;

import java.math.BigDecimal;

public interface TaxService {
    BigDecimal calculateFederalTax(double income, String filingStatus);
    BigDecimal calculateStateTax(double income, String stateCode, String filingStatus);
}
