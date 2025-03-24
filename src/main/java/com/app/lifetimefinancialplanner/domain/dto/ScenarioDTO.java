package com.app.lifetimefinancialplanner.domain.dto;

import lombok.Data;

@Data
public class ScenarioDTO {
    private Long userId;
    private String name;                            // scenarioName
    private String maritalStatus;                   // 'Y' or 'N'
    private Integer birthYearUser;
    private Integer birthYearSpouse;                // nullable
    private DistributionDTO lifeExpectancyUser;
    private DistributionDTO lifeExpectancySpouse;   // nullable
    private Double financialGoal;
    private Double preTaxContributionLimit;
    private Double afterTaxContributionLimit;
    private String stateOfResidence;
    private DistributionDTO inflationAssumption;
}
