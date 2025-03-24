package com.app.lifetimefinancialplanner.domain.dto;

public class InvestmentDTO {
    private Long id;
    private Long scenarioId;
    private Long investmentTypeId;
    private Double value;
    private String taxStatus;   // 'NON-RETIREMENT', 'PRE-TAX', 'AFTER-TAX'
}
