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
    public BigDecimal calculateFederalTax(BigDecimal income, String filingStatus) {
        // Set Standard Deduction in terms of filing status
        BigDecimal standardDeduction;
        if (filingStatus.equalsIgnoreCase("SINGLE")) {
            standardDeduction = BigDecimal.valueOf(14600);
        } else if (filingStatus.equalsIgnoreCase("MARRIED_JOINT")) {
            standardDeduction = BigDecimal.valueOf(29200);
        } else {
            throw new IllegalArgumentException("Invalid filing status: " + filingStatus);
        }

        BigDecimal taxableIncome = income.subtract(standardDeduction);
        // taxableIncome can't be negative (Set as 0)
        if (taxableIncome.compareTo(BigDecimal.ZERO) < 0) {
            taxableIncome = BigDecimal.ZERO;
        }

        List<TaxBracketDTO> brackets = federalTaxDTO.getFederalBrackets().get(filingStatus);
        if (brackets == null) {
            throw new IllegalArgumentException("No tax brackets found for filing status: " + filingStatus);
        }
        return calculateTaxFromBrackets(taxableIncome, brackets);
    }

    // Calculate state income tax based on income, state code, and filing status.
    public BigDecimal calculateStateTax(BigDecimal income, String stateCode, String filingStatus) {
        // Retrieve tax brackets for the given state and filing status from stateTaxDTO.
        List<TaxBracketDTO> brackets = stateTaxDTO.getStateBrackets().get(stateCode)
                .getBrackets().get(filingStatus);
        if (brackets == null) {
            // Return zero if no tax information is available for the given state and status.
            return BigDecimal.ZERO;
        }
        return calculateTaxFromBrackets(income, brackets);
    }

    // Calculate capital gains tax
    public BigDecimal calculateCapitalGainsTax(BigDecimal capitalGains, String filingStatus, String stateCode) {
        BigDecimal federalTax = calculateFederalTax(capitalGains, filingStatus);
        BigDecimal stateTax = calculateStateTax(capitalGains, stateCode, filingStatus);
        return federalTax.add(stateTax).setScale(2, RoundingMode.HALF_UP);
    }

    // Calculate early withdrawal tax at a fixed 10% rate
    public BigDecimal calculateEarlyWithdrawalTax(BigDecimal withdrawalAmount) {
        return withdrawalAmount.multiply(BigDecimal.valueOf(0.10)).setScale(2, RoundingMode.HALF_UP);
    }

    // Implement progressive tax calculation based on the given tax brackets.
    private BigDecimal calculateTaxFromBrackets(BigDecimal income, List<TaxBracketDTO> brackets) {
        BigDecimal taxResult = BigDecimal.ZERO;
        // Calculate the taxable amount for each bracket
        for (TaxBracketDTO bracket : brackets) {
            BigDecimal bracketMin = BigDecimal.valueOf(bracket.getMin());
            Double bracketMaxVal = bracket.getMax(); // Set as double since highest tax bracket can be null
            BigDecimal bracketMax = (bracketMaxVal != null) ? BigDecimal.valueOf(bracketMaxVal) : null;
            BigDecimal rate = BigDecimal.valueOf(bracket.getRate());

            // Income does not reach this bracket, move to next bracket
            if (income.compareTo(bracketMin) < 0) {
                continue;
            }
            BigDecimal taxableAmount;
            if (bracketMax != null && income.compareTo(bracketMax) < 0) {
                // Income is within this bracket, so calculate the taxableAmount
                taxableAmount = income.subtract(bracketMin);
                taxResult = taxResult.add(taxableAmount.multiply(rate));
                // No further brackets need processing.
                break;
            } else {
                // If income exceeds the bracket's max, tax the full range of the bracket.
                taxableAmount = (bracketMax != null ? bracketMax.subtract(bracketMin) : income.subtract(bracketMin));
                taxResult = taxResult.add(taxableAmount.multiply(rate));
            }
        }
        return taxResult.setScale(2, RoundingMode.HALF_UP);
    }
}
