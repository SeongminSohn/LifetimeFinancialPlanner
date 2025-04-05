package com.app.lifetimefinancialplanner.service;

import com.app.lifetimefinancialplanner.config.TaxDataLoader;
import com.app.lifetimefinancialplanner.domain.dto.FederalTaxDTO;
import com.app.lifetimefinancialplanner.domain.dto.StateTaxDTO;
import com.app.lifetimefinancialplanner.domain.dto.TaxBracketDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class TaxService {

    private final FederalTaxDTO federalTaxDTO;
    private final StateTaxDTO stateTaxDTO;

    public TaxService(TaxDataLoader taxDataLoader) {
        this.federalTaxDTO = taxDataLoader.getFederalTaxDTO();
        this.stateTaxDTO = taxDataLoader.getStateTaxDTO();
    }

    // Calculate federal income tax based on income and filing status.
    public BigDecimal calculateFederalTax(double income, String filingStatus) {
        // Set Standard Deduction in terms of filing status
        double standardDeduction;
        if (filingStatus.equalsIgnoreCase("SINGLE")) {
            standardDeduction = 14600;
        } else if (filingStatus.equalsIgnoreCase("MARRIED_JOINT")) {
            standardDeduction = 29200;
        } else {
            throw new IllegalArgumentException("Invalid filing status: " + filingStatus);
        }

        double taxableIncome = income - standardDeduction;
        // taxableIncome can't be negative (Set as 0)
        if (taxableIncome < 0) {
            taxableIncome = 0;
        }

        List<TaxBracketDTO> brackets = federalTaxDTO.getFederalBrackets().get(filingStatus);
        if (brackets == null) {
            throw new IllegalArgumentException("No tax brackets found for filing status: " + filingStatus);
        }
        return calculateTaxFromBrackets(taxableIncome, brackets);
    }

    // Calculate state income tax based on income, state code, and filing status.
    public BigDecimal calculateStateTax(double income, String stateCode, String filingStatus) {
        // Retrieve tax brackets for the given state and filing status from stateTaxDTO.
        List<TaxBracketDTO> brackets = stateTaxDTO.getStateBrackets().get(stateCode)
                .getBrackets().get(filingStatus);
        if (brackets == null) {
            // Return zero if no tax information is available for the given state and status.
            return BigDecimal.ZERO;
        }
        return calculateTaxFromBrackets(income, brackets);
    }

    // Implement progressive tax calculation based on the given tax brackets.
    private BigDecimal calculateTaxFromBrackets(double income, List<TaxBracketDTO> brackets) {
        BigDecimal taxResult = BigDecimal.ZERO;
        // Calculate the taxable amount for each bracket
        for (TaxBracketDTO bracket : brackets) {
            double bracketMin = bracket.getMin();
            Double bracketMax = bracket.getMax(); // can be null for highest tax bracket
            double rate = bracket.getRate();

            // Income does not reach this bracket, move to next bracket
            if (income < bracketMin) {
                continue;
            }
            // Income is greater than bracketMin
            double taxableAmount;
            if (bracketMax != null && income < bracketMax) {
                // Income is within this bracket, so calculate the taxableAmount
                taxableAmount = income - bracketMin;
                taxResult = taxResult.add(
                        BigDecimal.valueOf(taxableAmount).multiply(BigDecimal.valueOf(rate))
                );
                // No further brackets need processing.
                break;
            }
            else {
                // If income exceeds the bracket's max, tax the full range of the bracket.
                taxableAmount = (bracketMax == null ? income : bracketMax) - bracketMin;
                taxResult = taxResult.add(
                        BigDecimal.valueOf(taxableAmount).multiply(BigDecimal.valueOf(rate))
                );
            }
        }
        //
        return taxResult.setScale(2, RoundingMode.HALF_UP);
    }
}
