package com.app.lifetimefinancialplanner.domain.dto;

public class InvestmentDTO {
    private String investmentType; // 'Cash', 'S&P 500' or 'Municipal bonds'
    private Double value;
    private String taxStatus; // 'NonRetirement', 'PreTax', 'AfterTax'
}
