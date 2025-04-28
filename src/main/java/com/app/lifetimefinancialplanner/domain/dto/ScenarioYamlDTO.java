package com.app.lifetimefinancialplanner.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScenarioYamlDTO {
    private String name;
    private String maritalStatus;
    private List<Integer> birthYears;
    private List<DistributionDTO> lifeExpectancy;
    private List<InvestmentTypeDTO> investmentTypes;
    private List<InvestmentDTO> investments;
    private List<EventSeriesDTO> eventSeries;
    private DistributionDTO inflationAssumption;
    private Double afterTaxContributionLimit;
    private List<String> expenseWithdrawalStrategy;
    private Double financialGoal;

    @JsonProperty("residenceState")
    private String stateOfResidence;
}
