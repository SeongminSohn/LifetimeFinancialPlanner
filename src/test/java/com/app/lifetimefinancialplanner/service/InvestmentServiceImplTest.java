package com.app.lifetimefinancialplanner.service;

import com.app.lifetimefinancialplanner.domain.context.SimulationContext;
import com.app.lifetimefinancialplanner.domain.dto.DistributionDTO;
import com.app.lifetimefinancialplanner.domain.entity.Investment;
import com.app.lifetimefinancialplanner.domain.entity.InvestmentType;
import com.app.lifetimefinancialplanner.domain.entity.Scenario;
import com.app.lifetimefinancialplanner.repository.InvestmentRepository;
import com.app.lifetimefinancialplanner.repository.InvestmentTypeRepository;
import com.app.lifetimefinancialplanner.repository.ScenarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InvestmentServiceImplTest {

    private InvestmentServiceImpl investmentService;
    private InvestmentRepository investmentRepository;
    private InvestmentTypeRepository investmentTypeRepository;
    private ScenarioRepository scenarioRepository;
    private DistributionService distributionService;
    private SamplingService samplingService;

    @BeforeEach
    void setUp() {
        investmentRepository = mock(InvestmentRepository.class);
        investmentTypeRepository = mock(InvestmentTypeRepository.class);
        scenarioRepository = mock(ScenarioRepository.class);
        distributionService = mock(DistributionService.class);
        samplingService = mock(SamplingService.class);

        investmentService = new InvestmentServiceImpl(
                investmentRepository,
                investmentTypeRepository,
                scenarioRepository,
                samplingService,
                distributionService
        );
    }

    @Test
    void testUpdateInvestmentValues_CurrentYearCase() {
        // Arrange
        Scenario scenario = Scenario.builder().id(1L).build();
        InvestmentType investmentType = InvestmentType.builder()
                .id(1L)
                .expectedAnnualIncome(null)
                .expectedAnnualReturn(null)
                .expenseRatio(0.05)
                .taxability("Y")
                .build();

        Investment investment = Investment.builder()
                .id(1L)
                .investmentType(investmentType)
                .value(1000.0)
                .taxStatus("NON-RETIREMENT")
                .scenario(scenario)
                .build();

        DistributionDTO dummyDist = new DistributionDTO();
        when(distributionService.convertEmbeddableToDTO(any())).thenReturn(dummyDist);
        when(samplingService.sample(dummyDist)).thenReturn(100.0); // income & return = 150
        when(investmentRepository.findAllByScenarioId(scenario.getId()))
                .thenReturn(List.of(investment));

        SimulationContext context = new SimulationContext();
        context.setCurrentYear(LocalDateTime.now().getYear());
        context.setCurYearIncome(BigDecimal.ZERO);
        context.setInflationFactor(1.0);

        // Act
        investmentService.updateInvestmentValues(scenario, context);

        // Assert
        List<Investment> updated = context.getUpdatedInvestments();
        assertEquals(1, updated.size());

        Investment updatedInvestment = updated.get(0);
        double income = 100.0;
        double returnVal = 100.0;
        double initial = 1000.0;
        double average = (initial + (initial + income + returnVal)) / 2.0;
        double expense = average * 0.05;
        double expectedValue = initial + income + returnVal - expense;

        assertEquals(expectedValue, updatedInvestment.getValue(), 1e-6);
        // Use compareTo to for BigDecimal comparison
        assertEquals(0, BigDecimal.valueOf(income).compareTo(context.getCurYearIncome()));
    }

    @Test
    void testUpdateInvestmentValues_NonCurrentYearCase() {
        // Arrange
        Scenario scenario = Scenario.builder().id(2L).build();
        InvestmentType investmentType = InvestmentType.builder()
                .id(2L)
                .expectedAnnualIncome(null)
                .expectedAnnualReturn(null)
                .expenseRatio(0.05)
                .taxability("Y")
                .build();

        Investment investment = Investment.builder()
                .id(2L)
                .investmentType(investmentType)
                .value(2000.0)
                .taxStatus("NON-RETIREMENT")
                .scenario(scenario)
                .build();

        DistributionDTO dummyDist = new DistributionDTO();
        when(distributionService.convertEmbeddableToDTO(any())).thenReturn(dummyDist);
        when(samplingService.sample(dummyDist)).thenReturn(150.0); // income & return shoudl be 150

        SimulationContext context = new SimulationContext();
        // currentYear is not current year, so calculate based on the updatedInvestments of context
        context.setCurrentYear(LocalDateTime.now().getYear() - 1);
        context.setUpdatedInvestments(new ArrayList<>(List.of(investment)));
        context.setCurYearIncome(BigDecimal.ZERO);
        context.setInflationFactor(1.0);

        // Act
        investmentService.updateInvestmentValues(scenario, context);

        // Assert
        List<Investment> updated = context.getUpdatedInvestments();
        assertEquals(1, updated.size());

        Investment updatedInvestment = updated.get(0);
        double income = 150.0;
        double returnVal = 150.0;
        double initial = 2000.0;
        double average = (initial + (initial + income + returnVal)) / 2.0;
        double expense = average * 0.05;
        double expectedValue = initial + income + returnVal - expense;

        assertEquals(expectedValue, updatedInvestment.getValue(), 1e-6);
        // BigDecimal Comparison
        assertEquals(0, BigDecimal.valueOf(income).compareTo(context.getCurYearIncome()));
    }
}
