package com.app.lifetimefinancialplanner.service;

import com.app.lifetimefinancialplanner.domain.context.SimulationContext;
import com.app.lifetimefinancialplanner.domain.dto.DistributionDTO;
import com.app.lifetimefinancialplanner.domain.entity.EventSeries;
import com.app.lifetimefinancialplanner.domain.entity.IncomeEvent;
import com.app.lifetimefinancialplanner.domain.entity.Scenario;
import com.app.lifetimefinancialplanner.repository.IncomeEventRepository;
import com.app.lifetimefinancialplanner.repository.ScenarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class IncomeEventServiceImplRunIncomeEventsTest {

    private IncomeEventRepository incomeEventRepository;
    private ScenarioRepository scenarioRepository;
    private DistributionService distributionService;
    private SamplingService samplingService;
    private IncomeEventServiceImpl incomeEventService;

    @BeforeEach
    void setUp() {
        incomeEventRepository = mock(IncomeEventRepository.class);
        scenarioRepository = mock(ScenarioRepository.class);
        distributionService = mock(DistributionService.class);
        samplingService = mock(SamplingService.class);
        incomeEventService = new IncomeEventServiceImpl(
                incomeEventRepository,
                null,
                distributionService,
                scenarioRepository,
                samplingService
        );
    }

    @Test
    void testRunIncomeEvents_CurrentYearRegularIncome() {
        int now = LocalDateTime.now().getYear();
        Scenario scenario = Scenario.builder().id(1L).build();
        EventSeries eventSeries = EventSeries.builder()
                .id(10L)
                .startYear(null)
                .duration(null)
                .build();
        IncomeEvent incomeEvent = IncomeEvent.builder()
                .eventSeriesId(100L)
                .eventSeries(eventSeries)
                .initialAmount(1000.0)
                .annualChange(null)
                .inflationAdjustment("Y")
                .userPercentage(0.3)
                .isSocialSecurity("N")
                .build();
        SimulationContext context = new SimulationContext();
        context.setCurrentYear(now);
        context.setCurYearIncome(BigDecimal.ZERO);
        context.setCurYearSS(BigDecimal.ZERO);
        context.setInflationFactor(1.1);
        DistributionDTO dummyDist = new DistributionDTO();
        when(distributionService.convertEmbeddableToDTO(any())).thenReturn(dummyDist);
        when(samplingService.sample(dummyDist))
                .thenReturn((double)(now - 1), 3.0, 50.0);
        when(incomeEventRepository.findAllByEventSeries_Scenario_Id(scenario.getId()))
                .thenReturn(List.of(incomeEvent));
        incomeEventService.runIncomeEvents(scenario, context, true, true);
        assertEquals(1155.0, context.getCurYearIncome().doubleValue(), 1e-6);
        assertEquals(0.0, context.getCurYearSS().doubleValue(), 1e-6);
        assertNotNull(context.getUpdatedIncomeEvents());
        assertEquals(1, context.getUpdatedIncomeEvents().size());
        IncomeEvent updatedEvent = context.getUpdatedIncomeEvents().get(0);
        assertEquals(1155.0, updatedEvent.getInitialAmount(), 1e-6);
    }

    @Test
    void testRunIncomeEvents_NonCurrentYearRegularIncome() {
        int now = LocalDateTime.now().getYear();
        int nonCurrentYear = now - 1;
        Scenario scenario = Scenario.builder().id(2L).build();
        EventSeries eventSeries = EventSeries.builder()
                .id(20L)
                .startYear(null)
                .duration(null)
                .build();
        IncomeEvent incomeEvent = IncomeEvent.builder()
                .eventSeriesId(200L)
                .eventSeries(eventSeries)
                .initialAmount(2000.0)
                .annualChange(null)
                .inflationAdjustment("Y")
                .userPercentage(0.4)
                .isSocialSecurity("N")
                .build();
        SimulationContext context = new SimulationContext();
        context.setCurrentYear(nonCurrentYear);
        List<IncomeEvent> existingUpdatedEvents = new ArrayList<>();
        existingUpdatedEvents.add(incomeEvent);
        context.setUpdatedIncomeEvents(existingUpdatedEvents);
        context.setCurYearIncome(BigDecimal.ZERO);
        context.setCurYearSS(BigDecimal.ZERO);
        context.setInflationFactor(1.0);
        DistributionDTO dummyDist = new DistributionDTO();
        when(distributionService.convertEmbeddableToDTO(any())).thenReturn(dummyDist);
        when(samplingService.sample(dummyDist))
                .thenReturn((double)(nonCurrentYear - 1), 3.0, 100.0);
        incomeEventService.runIncomeEvents(scenario, context, true, false);
        assertEquals(840.0, context.getCurYearIncome().doubleValue(), 1e-6);
        assertEquals(0.0, context.getCurYearSS().doubleValue(), 1e-6);
        assertNotNull(context.getUpdatedIncomeEvents());
        assertEquals(1, context.getUpdatedIncomeEvents().size());
        IncomeEvent updatedEvent = context.getUpdatedIncomeEvents().get(0);
        assertEquals(840.0, updatedEvent.getInitialAmount(), 1e-6);
    }
}
