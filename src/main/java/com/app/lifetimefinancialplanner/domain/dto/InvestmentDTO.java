package com.app.lifetimefinancialplanner.domain.dto;

import lombok.Data;

@Data
public class InvestmentDTO {
    private Long id;
    private Long scenarioId;
    private Long investmentTypeId;
    private Double value;
    private String taxStatus;   // 'NON-RETIREMENT', 'PRE-TAX', 'AFTER-TAX'
}
