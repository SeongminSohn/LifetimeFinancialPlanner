package com.app.lifetimefinancialplanner.domain.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;


@Setter
@Getter
public class FederalTaxDTO {
    // "federalBrackets": { "SINGLE": [ {min:0, max:9950, rate:0.1}, ...], "MARRIED": [...], ... }
    private Map<String, List<TaxBracketDTO>> federalBrackets;
}

