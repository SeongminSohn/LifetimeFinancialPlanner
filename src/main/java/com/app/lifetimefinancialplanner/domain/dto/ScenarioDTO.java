package com.app.lifetimefinancialplanner.domain.dto;

import lombok.Data;

@Data
public class ScenarioDTO {
    private String name;                        // scenarioName
    private String maritalStatus;               // 'Y' or 'N'
    private Integer birthYearUser;
    private Integer birthYearSpouse;            // nullable
    private Integer lifeExpectancyUser;
    private Integer lifeExpectancySpouse;       // nullable
    private Double financialGoal;
    private Double preTaxContributionLimit;
    private Double afterTaxContributionLimit;
    private Long federalTaxInfoId;
    private Long stateTaxInfoId;
    private Long inflationAssumptionId;
    private String stateOfResidence;
}
