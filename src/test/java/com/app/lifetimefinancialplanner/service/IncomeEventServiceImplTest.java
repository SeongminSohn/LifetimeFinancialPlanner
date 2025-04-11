package com.app.lifetimefinancialplanner.service;

import com.app.lifetimefinancialplanner.domain.dto.DistributionDTO;
import com.app.lifetimefinancialplanner.domain.dto.IncomeEventDTO;
import com.app.lifetimefinancialplanner.domain.embeddable.DistributionEmbeddable;
import com.app.lifetimefinancialplanner.domain.entity.Scenario;
import com.app.lifetimefinancialplanner.repository.EventSeriesRepository;
import com.app.lifetimefinancialplanner.repository.IncomeEventRepository;
import com.app.lifetimefinancialplanner.repository.ScenarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class IncomeEventServiceImplTest {

    @Mock
    private IncomeEventRepository incomeEventRepository;
    @Mock
    private EventSeriesRepository eventSeriesRepository;
    @Mock
    private DistributionService distributionService;
    @Mock
    private ScenarioRepository scenarioRepository;

    @InjectMocks
    private IncomeEventServiceImpl incomeEventService;

    //Create Scenario and it can get the value of inflation value form specific Scenario ID
    private Scenario createScenario(Long id, double inflationValue) {
        DistributionEmbeddable inflationAssumption = new DistributionEmbeddable();
        inflationAssumption.setValue(inflationValue);
        return Scenario.builder()
                .id(id)
                .name("Test Scenario")
                .maritalStatus("S")
                .birthYearUser(1980)
                .financialGoal(1000.0)
                .inflationAssumption(inflationAssumption)
                .build();
    }

    //Income Event DTO
    private IncomeEventDTO createIncomeEventDTO(Long scenarioId,
                                                double initialAmount,
                                                double startYear,
                                                double duration,
                                                double annualChange,
                                                String inflationAdjustment,
                                                Double userPercentage) {
        IncomeEventDTO dto = new IncomeEventDTO();
        dto.setScenarioId(scenarioId);
        dto.setInitialAmount(initialAmount);

        DistributionDTO startYearDto = new DistributionDTO();
        startYearDto.setValue(startYear);
        dto.setStartYear(startYearDto);

        DistributionDTO durationDto = new DistributionDTO();
        durationDto.setValue(duration);
        dto.setDuration(durationDto);

        DistributionDTO annualChangeDto = new DistributionDTO();
        annualChangeDto.setValue(annualChange);
        dto.setAnnualChange(annualChangeDto);

        dto.setInflationAdjustment(inflationAdjustment);
        dto.setUserPercentage(userPercentage);
        return dto;
    }

     // if Inflation is "Y"
     // 1000 + 50 = 1050 -> 1050 * 1.02 = 1071  1071 * 1.1 = 1178.1
//    @Test
//    void testRunIncomeEvents_TriggeredWithInflation() {
//        Long scenarioId = 1L;
//        double initialAmount = 1000.0;
//        double startYear = 2025.0;
//        double duration = 10.0;
//        double annualChange = 50.0;
//        String inflationAdjustment = "Y";
//        Double userPercentage = 0.1;
//        // (2025 + 10 = 2035)
//        int currentYear = 2035;
//
//        IncomeEventDTO dto = createIncomeEventDTO(scenarioId, initialAmount, startYear, duration, annualChange, inflationAdjustment, userPercentage);
//
//        // 2% for inflation Assumption
//        Scenario scenario = createScenario(scenarioId, 0.02);
//        when(scenarioRepository.findById(scenarioId)).thenReturn(Optional.of(scenario));
//
//        incomeEventService.runIncomeEvents(dto, currentYear, 0.0);
//
//        // Then the result should be 1178.1
//        double expected = 1178.1;
//        assertEquals(expected, dto.getInitialAmount(), 0.001);
//    }
//
//    //the calculation should not be implemented
//    @Test
//    void testRunIncomeEvents_NotTriggeredWithinEventPeriod() {
//        // Given
//        Long scenarioId = 1L;
//        double initialAmount = 1000.0;
//        double startYear = 2025.0;
//        double duration = 10.0;
//        double annualChange = 50.0;
//        String inflationAdjustment = "Y";
//        Double userPercentage = 0.1;
//        // currentYear => (2025 <= currentYear < 2035)
//        int currentYear = 2030;
//
//        IncomeEventDTO dto = createIncomeEventDTO(scenarioId, initialAmount, startYear, duration, annualChange, inflationAdjustment, userPercentage);
//
//        // It should not affect the calulation
//        Scenario scenario = createScenario(scenarioId, 0.02);
//        when(scenarioRepository.findById(scenarioId)).thenReturn(Optional.of(scenario));
//        incomeEventService.runIncomeEvents(dto, currentYear, 0.0);
//
//        // Then: the result has to be same with the initial amount
//        assertEquals(initialAmount, dto.getInitialAmount());
//    }
//
//    //inflation is not applied.
//    @Test
//    void testRunIncomeEvents_TriggeredWithoutInflation() {
//        Long scenarioId = 1L;
//        double initialAmount = 1000.0;
//        double startYear = 2025.0;
//        double duration = 10.0;
//        double annualChange = 50.0;
//        String inflationAdjustment = "N";
//        Double userPercentage = 0.1;
//        int currentYear = 2035;
//
//        IncomeEventDTO dto = createIncomeEventDTO(scenarioId, initialAmount, startYear, duration, annualChange, inflationAdjustment, userPercentage);
//
//        // Inflation is N so the inflation should not be applied
//        Scenario scenario = createScenario(scenarioId, 0.02);
//        when(scenarioRepository.findById(scenarioId)).thenReturn(Optional.of(scenario));
//        incomeEventService.runIncomeEvents(dto, currentYear, 0.0);
//
//        // Then reulst shuold be 1155
//        double expected = 1155.0;
//        assertEquals(expected, dto.getInitialAmount(), 0.001);
//    }
}
