package com.app.lifetimefinancialplanner.service;

import com.app.lifetimefinancialplanner.domain.entity.Scenario;
import com.app.lifetimefinancialplanner.domain.dto.SimulationDTO;
import com.app.lifetimefinancialplanner.repository.ScenarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SimulationServiceImplTest {
    @InjectMocks
    private SimulationServiceImpl simulationService;

    @Mock
    private ScenarioRepository scenarioRepository;
    @Mock
    private IncomeEventService incomeEventService;
    @Mock
    private ExpenseEventService expenseEventService;
    @Mock
    private InvestmentService investmentService;
    @Mock
    private InvestEventService investEventService;
    @Mock
    private ExpenseWithdrawalStrategyService withdrawalService;
    @Mock
    private SamplingService samplingService;
    @Mock
    private DistributionService distributionService;
    @Mock
    private TaxService taxService;
    @Mock
    private LogService logService;

    private Scenario dummyScenario;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        // 시나리오 리포 설정
        dummyScenario = Scenario.builder()
                .id(42L)
                .birthYearUser(1980)
                .afterTaxContributionLimit(5000.0)
                .financialGoal(10000.0)
                .stateOfResidence("NY")
                .build();
        when(scenarioRepository.findById(42L)).thenReturn(Optional.of(dummyScenario));
        when(samplingService.sample(any())).thenReturn(1.0);
    }

    @Test
    void runSimulation_shouldReturnSuccess_whenGoalReached() {
        List<SimulationDTO> results = simulationService.runSimulation(42L, 1);

        assertEquals(1, results.size());
        SimulationDTO dto = results.get(0);
        assertNotNull(dto.getSimulationYears());
        assertTrue(dto.getResult().matches("SUCCESS|FAIL"));
        assertNotNull(dto.getBatchId());
        verify(scenarioRepository).findById(42L);
    }

    @Test
    void runSimulation_invalidScenario_shouldThrow() {
        when(scenarioRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> simulationService.runSimulation(99L, 1));
    }
}
