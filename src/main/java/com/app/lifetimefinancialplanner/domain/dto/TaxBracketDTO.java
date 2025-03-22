package com.app.lifetimefinancialplanner.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TaxBracketDTO {
    private double min;
    private Double max;  // null if no upper bound
    private double rate;
}

