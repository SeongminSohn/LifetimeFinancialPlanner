package com.app.lifetimefinancialplanner.service;

import com.app.lifetimefinancialplanner.domain.dto.InvestmentDTO;
import com.app.lifetimefinancialplanner.domain.entity.Investment;
import com.app.lifetimefinancialplanner.domain.entity.InvestmentType;
import com.app.lifetimefinancialplanner.domain.entity.Scenario;
import com.app.lifetimefinancialplanner.repository.InvestmentRepository;
import com.app.lifetimefinancialplanner.repository.InvestmentTypeRepository;
import com.app.lifetimefinancialplanner.repository.ScenarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InvestmentServiceImplTest {

    @InjectMocks
    private InvestmentServiceImpl investmentService;

    @Mock
    private InvestmentRepository investmentRepository;

    @Mock
    private InvestmentTypeRepository investmentTypeRepository;

    @Mock
    private ScenarioRepository scenarioRepository;

    @Mock
    private DistributionService distributionService;

    @Mock
    private SamplingService samplingService;

    private Scenario mockScenario;
    private InvestmentType mockType;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Prepare a dummy Scenario
        mockScenario = Scenario.builder()
                .id(1L)
                .name("Test Scenario")
                .birthYearUser(1990)
                .afterTaxContributionLimit(5000.0)
                .financialGoal(10000.0)
                // other fields can be null if not required by this test
                .build();

        // Prepare a dummy InvestmentType
        mockType = InvestmentType.builder()
                .id(2L)
                .name("TestType")
                .description("description")
                .taxability("Y")
                .build();

        when(scenarioRepository.findById(1L)).thenReturn(Optional.of(mockScenario));
        when(investmentTypeRepository.findById(2L)).thenReturn(Optional.of(mockType));
    }

    @Test
    void createInvestment_success() {
        // Given: an InvestmentDTO for creation
        InvestmentDTO dto = new InvestmentDTO();
        dto.setScenarioId(1L);
        dto.setInvestmentTypeId(2L);
        dto.setValue(1234.5);
        dto.setTaxStatus("NON-RETIREMENT");

        // When saving, repository returns an entity with generated ID
        Investment savedEntity = Investment.builder()
                .id(10L)
                .scenario(mockScenario)
                .investmentType(mockType)
                .value(dto.getValue())
                .taxStatus(dto.getTaxStatus())
                .createdAt(LocalDateTime.now())
                .build();
        when(investmentRepository.save(any(Investment.class))).thenReturn(savedEntity);

        // Act
        Investment result = investmentService.createInvestment(dto);

        // Assert
        assertNotNull(result.getId(), "Saved investment should have an ID");
        assertEquals(dto.getValue(), result.getValue(), "Investment value should match DTO");
        assertEquals(dto.getTaxStatus(), result.getTaxStatus(), "Tax status should match DTO");
        verify(investmentRepository, times(1)).save(any(Investment.class));
    }

    @Test
    void updateInvestment_changeValueAndStatus() {
        // Given: an existing Investment in repository
        Investment existing = Investment.builder()
                .id(20L)
                .scenario(mockScenario)
                .investmentType(mockType)
                .value(500.0)
                .taxStatus("AFTER-TAX")
                .createdAt(LocalDateTime.now())
                .build();
        when(investmentRepository.findById(20L)).thenReturn(Optional.of(existing));

        // Prepare an update DTO
        InvestmentDTO updateDto = new InvestmentDTO();
        updateDto.setValue(750.0);
        updateDto.setTaxStatus("PRE-TAX");
        // scenarioId null means no change
        updateDto.setScenarioId(null);

        // When saving, repository returns the modified entity
        Investment updatedEntity = existing.toBuilder()
                .value(updateDto.getValue())
                .taxStatus(updateDto.getTaxStatus())
                .build();
        when(investmentRepository.save(any(Investment.class))).thenReturn(updatedEntity);

        // Act
        Investment result = investmentService.updateInvestment(20L, updateDto);

        // Assert
        assertEquals(750.0, result.getValue(), "Value should be updated to 750.0");
        assertEquals("PRE-TAX", result.getTaxStatus(), "Tax status should be updated to PRE-TAX");
        verify(investmentRepository, times(1)).save(any(Investment.class));
    }

    @Test
    void deleteInvestment_success() {
        // Given: an existing Investment for deletion
        Investment existing = Investment.builder().id(30L).build();
        when(investmentRepository.findById(30L)).thenReturn(Optional.of(existing));

        // Act
        investmentService.deleteInvestment(30L);

        // Assert
        verify(investmentRepository, times(1)).delete(existing);
    }
}
