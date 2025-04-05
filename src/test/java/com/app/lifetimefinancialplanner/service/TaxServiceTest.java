package com.app.lifetimefinancialplanner.service;

import com.app.lifetimefinancialplanner.config.TaxDataLoader;
import com.app.lifetimefinancialplanner.domain.dto.FederalTaxDTO;
import com.app.lifetimefinancialplanner.domain.dto.StateTaxDTO;
import com.app.lifetimefinancialplanner.domain.dto.TaxBracketDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TaxServiceTest {

    @Mock
    private TaxDataLoader taxDataLoader;

    private TaxService taxService;

    @BeforeEach
    void setUp() {
        // Set up sample federal tax brackets for "SINGLE" filing status.
        Map<String, List<TaxBracketDTO>> federalBrackets = new HashMap<>();
        List<TaxBracketDTO> singleFederalBrackets = new ArrayList<>();

        TaxBracketDTO fBracket1 = new TaxBracketDTO();
        fBracket1.setMin(0);
        fBracket1.setMax(11925.0);
        fBracket1.setRate(0.10);
        singleFederalBrackets.add(fBracket1);

        TaxBracketDTO fBracket2 = new TaxBracketDTO();
        fBracket2.setMin(11925.0);
        fBracket2.setMax(null);  // No upper bound for the last bracket
        fBracket2.setRate(0.12);
        singleFederalBrackets.add(fBracket2);

        federalBrackets.put("SINGLE", singleFederalBrackets);
        FederalTaxDTO federalTaxDTO = new FederalTaxDTO();
        federalTaxDTO.setFederalBrackets(federalBrackets);

        // Set up sample state tax brackets for NY "SINGLE" filing status.
        // For NY, we use a single bracket for simplicity in this test.
        Map<String, StateTaxDTO.StateTaxData> stateDataMap = new HashMap<>();
        StateTaxDTO.StateTaxData nyData = new StateTaxDTO.StateTaxData();
        List<TaxBracketDTO> nySingleBrackets = new ArrayList<>();
        TaxBracketDTO nyBracket = new TaxBracketDTO();
        nyBracket.setMin(0);
        nyBracket.setMax(null);
        nyBracket.setRate(0.06);
        nySingleBrackets.add(nyBracket);
        nyData.getBrackets().put("SINGLE", nySingleBrackets);
        stateDataMap.put("NY", nyData);

        StateTaxDTO stateTaxDTO = new StateTaxDTO();
        stateTaxDTO.setStateBrackets(stateDataMap);

        // Configure the TaxDataLoader mock to return our sample DTOs
        when(taxDataLoader.getFederalTaxDTO()).thenReturn(federalTaxDTO);
        when(taxDataLoader.getStateTaxDTO()).thenReturn(stateTaxDTO);

        taxService = new TaxService(taxDataLoader);
    }

    @Test
    void testCalculateFederalTax_Single() {
        // For federal tax:
        // Assume income is 60000.
        // Standard deduction for SINGLE is 14600.
        // Taxable income = 60000 - 14600 = 45400.
        // For the first bracket: from 0 to 11925 at 10% => tax = (11925 - 0) * 0.10 = 1192.5.
        // For the second bracket: income falls in this bracket, so tax = (45400 - 11925) * 0.12.
        // Calculate expected tax.
        double income = 60000;
        BigDecimal tax = taxService.calculateFederalTax(income, "SINGLE");

        double taxableIncome = 60000 - 14600;  // 45400
        double taxBracket1 = (11925 - 0) * 0.10; // 1192.5
        double taxBracket2 = (taxableIncome - 11925) * 0.12; // (45400 - 11925) * 0.12
        double expectedTax = taxBracket1 + taxBracket2;
        BigDecimal expected = BigDecimal.valueOf(expectedTax).setScale(2, RoundingMode.HALF_UP);

        assertEquals(expected, tax);
    }

    @Test
    void testCalculateStateTax_Single() {
        // For state tax (NY SINGLE): Assume income is 60000.
        // In our test setup, we use a single bracket of 0 to infinity at 6%.
        double income = 60000;
        BigDecimal tax = taxService.calculateStateTax(income, "NY", "SINGLE");
        BigDecimal expected = BigDecimal.valueOf(income * 0.06).setScale(2, RoundingMode.HALF_UP);
        assertEquals(expected, tax);
    }
}
