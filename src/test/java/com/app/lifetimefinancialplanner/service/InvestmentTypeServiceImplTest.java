// InvestmentTypeServiceImplTest.java
package com.app.lifetimefinancialplanner.service;

import com.app.lifetimefinancialplanner.domain.dto.DistributionDTO;
import com.app.lifetimefinancialplanner.domain.dto.InvestmentTypeDTO;
import com.app.lifetimefinancialplanner.domain.embeddable.DistributionEmbeddable;
import com.app.lifetimefinancialplanner.domain.entity.InvestmentType;
import com.app.lifetimefinancialplanner.domain.entity.Scenario;
import com.app.lifetimefinancialplanner.repository.InvestmentTypeRepository;
import com.app.lifetimefinancialplanner.repository.ScenarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InvestmentTypeServiceImplTest {

    @Mock
    private InvestmentTypeRepository investmentTypeRepository;
    @Mock
    private ScenarioRepository scenarioRepository;
    @Mock
    private DistributionService distributionService;
    @InjectMocks
    private InvestmentTypeServiceImpl investmentTypeService;

    private Scenario createScenario(Long id) {
        // Helper to create a Scenario entity with the given id (and minimal required fields)
        Scenario scenario = Scenario.builder()
                .id(id)
                .name("Test Scenario")
                .maritalStatus("S")
                .birthYearUser(1980)
                .financialGoal(1000.0)
                .build();
        return scenario;
    }

    private DistributionDTO createDistributionDTO(String type, String amountOrPercent, Double value) {
        // Helper to create a DistributionDTO with basic fields (e.g., for FIXED distribution)
        DistributionDTO dto = new DistributionDTO();
        dto.setDistributionType(type);
        dto.setAmountOrPercent(amountOrPercent);
        dto.setValue(value);
        return dto;
    }

    private DistributionEmbeddable createDistributionEmbeddable(String type, String amountOrPercent, Double value) {
        // Helper to create a DistributionEmbeddable with basic fields corresponding to DistributionDTO
        DistributionEmbeddable emb = new DistributionEmbeddable();
        emb.setDistributionType(type);
        emb.setAmountOrPercent(amountOrPercent);
        emb.setValue(value);
        emb.setLower(null);
        emb.setUpper(null);
        emb.setMean(null);
        emb.setStDev(null);
        return emb;
    }

    @BeforeEach
    void setUp() {
        // Initialize mocks (automatically done by MockitoExtension) and any common setup
    }

    @Test
    void testCreateInvestmentType_Success() {
        // Given: a valid InvestmentTypeDTO and existing Scenario in repository
        Long scenarioId = 1L;
        Scenario scenario = createScenario(scenarioId);
        InvestmentTypeDTO inputDto = new InvestmentTypeDTO();
        inputDto.setScenarioId(scenarioId);
        inputDto.setName("New Investment");
        inputDto.setDescription("New investment description");
        inputDto.setExpenseRatio(0.15);
        inputDto.setTaxability("Y");
        // Prepare DistributionDTOs for expectedAnnualReturn and expectedAnnualIncome
        DistributionDTO returnDto = createDistributionDTO("FIXED", "PERCENT", 5.0);
        DistributionDTO incomeDto = createDistributionDTO("FIXED", "AMOUNT", 2000.0);
        inputDto.setExpectedAnnualReturn(returnDto);
        inputDto.setExpectedAnnualIncome(incomeDto);
        // Prepare corresponding DistributionEmbeddables that the DistributionService should return
        DistributionEmbeddable returnEmb = createDistributionEmbeddable("FIXED", "PERCENT", 5.0);
        DistributionEmbeddable incomeEmb = createDistributionEmbeddable("FIXED", "AMOUNT", 2000.0);

        // Stub scenarioRepository and distributionService
        when(scenarioRepository.findById(scenarioId)).thenReturn(Optional.of(scenario));
        when(distributionService.convertDTOToEmbeddable(returnDto)).thenReturn(returnEmb);
        when(distributionService.convertDTOToEmbeddable(incomeDto)).thenReturn(incomeEmb);

        // Prepare an InvestmentType entity to simulate repository saving (with ID assigned after save)
        InvestmentType savedEntity = InvestmentType.builder()
                .id(100L)
                .scenario(scenario)
                .name(inputDto.getName())
                .description(inputDto.getDescription())
                .expectedAnnualReturn(returnEmb)
                .expenseRatio(inputDto.getExpenseRatio())
                .expectedAnnualIncome(incomeEmb)
                .taxability(inputDto.getTaxability())
                .createdAt(java.time.LocalDateTime.now())
                .build();
        when(investmentTypeRepository.save(any(InvestmentType.class))).thenReturn(savedEntity);

        // When: calling createInvestmentType on the service
        InvestmentType result = investmentTypeService.createInvestmentType(inputDto);

        // Then: verify that scenarioRepository was called and distributionService was used to convert distributions
        verify(scenarioRepository, times(1)).findById(scenarioId);
        verify(distributionService, times(1)).convertDTOToEmbeddable(returnDto);
        verify(distributionService, times(1)).convertDTOToEmbeddable(incomeDto);
        // Capture the InvestmentType passed to repository.save and verify its fields
        ArgumentCaptor<InvestmentType> captor = ArgumentCaptor.forClass(InvestmentType.class);
        verify(investmentTypeRepository).save(captor.capture());
        InvestmentType captured = captor.getValue();
        assertNull(captured.getId(), "New InvestmentType id should be null before save");
        assertEquals(scenario, captured.getScenario(), "Scenario should match the provided scenario");
        assertEquals(inputDto.getName(), captured.getName());
        assertEquals(inputDto.getDescription(), captured.getDescription());
        assertEquals(inputDto.getExpenseRatio(), captured.getExpenseRatio());
        assertEquals(inputDto.getTaxability(), captured.getTaxability());
        assertSame(returnEmb, captured.getExpectedAnnualReturn(), "ExpectedAnnualReturn embeddable should come from distributionService");
        assertSame(incomeEmb, captured.getExpectedAnnualIncome(), "ExpectedAnnualIncome embeddable should come from distributionService");
        // Verify the result returned from service is the saved entity and has the expected values (including assigned id)
        assertSame(savedEntity, result, "Returned InvestmentType should be the same as saved entity");
        assertEquals(100L, result.getId());
        assertEquals(inputDto.getName(), result.getName());
        assertEquals(inputDto.getDescription(), result.getDescription());
        assertEquals(inputDto.getExpenseRatio(), result.getExpenseRatio());
        assertEquals(inputDto.getTaxability(), result.getTaxability());
        assertSame(returnEmb, result.getExpectedAnnualReturn());
        assertSame(incomeEmb, result.getExpectedAnnualIncome());
    }

    @Test
    void testCreateInvestmentType_ScenarioNotFound() {
        // Given: an InvestmentTypeDTO with a scenarioId that is not present in repository
        Long scenarioId = 99L;
        InvestmentTypeDTO inputDto = new InvestmentTypeDTO();
        inputDto.setScenarioId(scenarioId);
        inputDto.setName("Investment without scenario");
        // scenarioRepository returns empty for this id
        when(scenarioRepository.findById(scenarioId)).thenReturn(Optional.empty());

        // When & Then: calling createInvestmentType should throw a RuntimeException for missing scenario
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> investmentTypeService.createInvestmentType(inputDto));
        assertTrue(exception.getMessage().contains("Scenario not found with id: " + scenarioId));
        // Verify that distributionService and repository were never called due to exception
        verify(scenarioRepository).findById(scenarioId);
        verify(investmentTypeRepository, never()).save(any());
        verifyNoInteractions(distributionService);
    }

    @Test
    void testGetInvestmentType_Found() {
        // Given: an existing InvestmentType in the repository
        Long investTypeId = 10L;
        Scenario scenario = createScenario(1L);
        InvestmentType investmentType = InvestmentType.builder()
                .id(investTypeId)
                .scenario(scenario)
                .name("Existing Investment")
                .description("Existing description")
                .expectedAnnualReturn(null)
                .expenseRatio(0.2)
                .expectedAnnualIncome(null)
                .taxability("N")
                .build();
        when(investmentTypeRepository.findById(investTypeId)).thenReturn(Optional.of(investmentType));

        // When: calling getInvestmentType
        Optional<InvestmentType> resultOpt = investmentTypeService.getInvestmentType(investTypeId);

        // Then: the Optional is present and contains the expected InvestmentType
        assertTrue(resultOpt.isPresent(), "Result should be present for an existing id");
        assertSame(investmentType, resultOpt.get(), "Returned optional should contain the exact InvestmentType");
        verify(investmentTypeRepository).findById(investTypeId);
    }

    @Test
    void testGetInvestmentType_NotFound() {
        // Given: no InvestmentType exists for the provided id
        Long investTypeId = 20L;
        when(investmentTypeRepository.findById(investTypeId)).thenReturn(Optional.empty());

        // When: calling getInvestmentType
        Optional<InvestmentType> resultOpt = investmentTypeService.getInvestmentType(investTypeId);

        // Then: the Optional is empty
        assertFalse(resultOpt.isPresent(), "Result should be empty for a non-existing id");
        verify(investmentTypeRepository).findById(investTypeId);
    }

    @Test
    void testUpdateInvestmentType_SuccessNoScenarioChange() {
        // Given: an existing InvestmentType and an update DTO that does not change scenario (scenarioId is null)
        Long investTypeId = 50L;
        Scenario originalScenario = createScenario(3L);
        // Existing InvestmentType with certain fields
        DistributionEmbeddable oldReturnEmb = createDistributionEmbeddable("FIXED", "PERCENT", 4.0);
        DistributionEmbeddable oldIncomeEmb = createDistributionEmbeddable("FIXED", "AMOUNT", 1000.0);
        InvestmentType existingInvestmentType = InvestmentType.builder()
                .id(investTypeId)
                .scenario(originalScenario)
                .name("Old Name")
                .description("Old Description")
                .expectedAnnualReturn(oldReturnEmb)
                .expenseRatio(0.10)
                .expectedAnnualIncome(oldIncomeEmb)
                .taxability("Y")
                .createdAt(java.time.LocalDateTime.now())
                .build();
        when(investmentTypeRepository.findById(investTypeId)).thenReturn(Optional.of(existingInvestmentType));
        // Prepare update DTO: scenarioId null (no change in scenario), some fields null to keep old values
        InvestmentTypeDTO updateDto = new InvestmentTypeDTO();
        updateDto.setScenarioId(null); // no scenario change
        updateDto.setName("New Name"); // provide new name
        updateDto.setDescription(null); // no new description, should retain old
        updateDto.setExpenseRatio(null); // no new expense ratio, should retain old
        updateDto.setTaxability("N"); // provide new taxability
        // For distributions: provide a new expectedAnnualReturn, leave expectedAnnualIncome null (to keep old)
        DistributionDTO newReturnDto = createDistributionDTO("FIXED", "PERCENT", 6.0);
        updateDto.setExpectedAnnualReturn(newReturnDto);
        updateDto.setExpectedAnnualIncome(null);
        // Prepare DistributionService outputs for provided distribution DTO (and handle null for income)
        DistributionEmbeddable newReturnEmb = createDistributionEmbeddable("FIXED", "PERCENT", 6.0);
        when(distributionService.convertDTOToEmbeddable(newReturnDto)).thenReturn(newReturnEmb);
        when(distributionService.convertDTOToEmbeddable(null)).thenReturn(null);
        // We do not call scenarioRepository in this case (scenarioId is null)
        // Prepare the updated InvestmentType that repository.save should return
        InvestmentType updatedInvestmentType = existingInvestmentType.toBuilder()
                .name("New Name")
                .description("Old Description") // retained old since input null
                .expenseRatio(0.10)            // retained old since input null
                .taxability("N")
                .expectedAnnualReturn(newReturnEmb)
                .expectedAnnualIncome(oldIncomeEmb) // retained old since input null
                .build();
        when(investmentTypeRepository.save(any(InvestmentType.class))).thenReturn(updatedInvestmentType);

        // When: calling updateInvestmentType with no scenario change
        InvestmentType result = investmentTypeService.updateInvestmentType(investTypeId, updateDto);

        // Then: verify repository and service interactions
        verify(investmentTypeRepository).findById(investTypeId);
        verify(scenarioRepository, never()).findById(any()); // scenarioRepository should not be called
        verify(distributionService).convertDTOToEmbeddable(newReturnDto);
        verify(distributionService).convertDTOToEmbeddable(isNull());
        verify(investmentTypeRepository).save(any(InvestmentType.class));
        // Capture the InvestmentType passed to save and verify its fields
        ArgumentCaptor<InvestmentType> captor = ArgumentCaptor.forClass(InvestmentType.class);
        verify(investmentTypeRepository).save(captor.capture());
        InvestmentType captured = captor.getValue();
        // The captured InvestmentType should reflect updates in certain fields and retention of others
        assertEquals(existingInvestmentType.getId(), captured.getId(), "ID should remain unchanged");
        assertEquals(originalScenario, captured.getScenario(), "Scenario should remain the same");
        assertEquals("New Name", captured.getName());
        assertEquals("Old Description", captured.getDescription(), "Description should remain unchanged");
        assertEquals(0.10, captured.getExpenseRatio(), 0.0001, "Expense ratio should remain unchanged");
        assertEquals("N", captured.getTaxability());
        assertSame(newReturnEmb, captured.getExpectedAnnualReturn(), "ExpectedAnnualReturn should be updated to new value");
        assertSame(oldIncomeEmb, captured.getExpectedAnnualIncome(), "ExpectedAnnualIncome should remain the old value");
        // The result returned should be the updatedInvestmentType from repository.save
        assertSame(updatedInvestmentType, result);
        assertEquals("New Name", result.getName());
        assertEquals("Old Description", result.getDescription());
        assertEquals(0.10, result.getExpenseRatio(), 0.0001);
        assertEquals("N", result.getTaxability());
        assertSame(newReturnEmb, result.getExpectedAnnualReturn());
        assertSame(oldIncomeEmb, result.getExpectedAnnualIncome());
    }

    @Test
    void testUpdateInvestmentType_SuccessChangeScenario() {
        // Given: an existing InvestmentType and an update DTO that specifies a new scenario
        Long investTypeId = 60L;
        Scenario originalScenario = createScenario(5L);
        Scenario newScenario = createScenario(6L);
        // Existing investment type
        DistributionEmbeddable origReturnEmb = createDistributionEmbeddable("FIXED", "PERCENT", 3.0);
        DistributionEmbeddable origIncomeEmb = createDistributionEmbeddable("FIXED", "AMOUNT", 500.0);
        InvestmentType existingInvestmentType = InvestmentType.builder()
                .id(investTypeId)
                .scenario(originalScenario)
                .name("Investment X")
                .description("Desc X")
                .expectedAnnualReturn(origReturnEmb)
                .expenseRatio(0.05)
                .expectedAnnualIncome(origIncomeEmb)
                .taxability("Y")
                .build();
        when(investmentTypeRepository.findById(investTypeId)).thenReturn(Optional.of(existingInvestmentType));
        // Update DTO with a different scenarioId and new values for all fields
        InvestmentTypeDTO updateDto = new InvestmentTypeDTO();
        updateDto.setScenarioId(newScenario.getId());  // request to change scenario
        updateDto.setName("Investment X Updated");
        updateDto.setDescription("Desc X Updated");
        updateDto.setExpenseRatio(0.08);
        updateDto.setTaxability("N");
        DistributionDTO newReturnDto = createDistributionDTO("FIXED", "PERCENT", 7.0);
        DistributionDTO newIncomeDto = createDistributionDTO("FIXED", "AMOUNT", 800.0);
        updateDto.setExpectedAnnualReturn(newReturnDto);
        updateDto.setExpectedAnnualIncome(newIncomeDto);
        // Stub scenarioRepository to return the new scenario
        when(scenarioRepository.findById(newScenario.getId())).thenReturn(Optional.of(newScenario));
        // Stub distributionService for new distribution values
        DistributionEmbeddable newReturnEmb = createDistributionEmbeddable("FIXED", "PERCENT", 7.0);
        DistributionEmbeddable newIncomeEmb = createDistributionEmbeddable("FIXED", "AMOUNT", 800.0);
        when(distributionService.convertDTOToEmbeddable(newReturnDto)).thenReturn(newReturnEmb);
        when(distributionService.convertDTOToEmbeddable(newIncomeDto)).thenReturn(newIncomeEmb);
        // Prepare updated entity to return from save
        InvestmentType updatedInvestmentType = existingInvestmentType.toBuilder()
                .scenario(newScenario)
                .name("Investment X Updated")
                .description("Desc X Updated")
                .expenseRatio(0.08)
                .taxability("N")
                .expectedAnnualReturn(newReturnEmb)
                .expectedAnnualIncome(newIncomeEmb)
                .build();
        when(investmentTypeRepository.save(any(InvestmentType.class))).thenReturn(updatedInvestmentType);

        // When: calling updateInvestmentType with a new scenario
        InvestmentType result = investmentTypeService.updateInvestmentType(investTypeId, updateDto);

        // Then: verify interactions and result
        verify(investmentTypeRepository).findById(investTypeId);
        verify(scenarioRepository).findById(newScenario.getId());
        verify(distributionService).convertDTOToEmbeddable(newReturnDto);
        verify(distributionService).convertDTOToEmbeddable(newIncomeDto);
        verify(investmentTypeRepository).save(any(InvestmentType.class));
        // Capture the saved InvestmentType to verify fields
        ArgumentCaptor<InvestmentType> captor = ArgumentCaptor.forClass(InvestmentType.class);
        verify(investmentTypeRepository).save(captor.capture());
        InvestmentType captured = captor.getValue();
        assertEquals(existingInvestmentType.getId(), captured.getId());
        assertEquals(newScenario, captured.getScenario(), "Scenario should be updated to new scenario");
        assertEquals("Investment X Updated", captured.getName());
        assertEquals("Desc X Updated", captured.getDescription());
        assertEquals(0.08, captured.getExpenseRatio(), 0.0001);
        assertEquals("N", captured.getTaxability());
        assertSame(newReturnEmb, captured.getExpectedAnnualReturn());
        assertSame(newIncomeEmb, captured.getExpectedAnnualIncome());
        // Verify the returned result is the updated entity
        assertSame(updatedInvestmentType, result);
        assertEquals(newScenario, result.getScenario());
        assertEquals("Investment X Updated", result.getName());
        assertEquals("Desc X Updated", result.getDescription());
        assertEquals(0.08, result.getExpenseRatio(), 0.0001);
        assertEquals("N", result.getTaxability());
        assertSame(newReturnEmb, result.getExpectedAnnualReturn());
        assertSame(newIncomeEmb, result.getExpectedAnnualIncome());
    }

    @Test
    void testUpdateInvestmentType_InvestmentTypeNotFound() {
        // Given: repository has no InvestmentType with the given id
        Long investTypeId = 70L;
        when(investmentTypeRepository.findById(investTypeId)).thenReturn(Optional.empty());

        // When & Then: calling updateInvestmentType should throw exception
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> investmentTypeService.updateInvestmentType(investTypeId, new InvestmentTypeDTO()));
        assertTrue(exception.getMessage().contains("InvestmentType not found"));
        // Verify that no further interactions occurred after the not-found
        verify(investmentTypeRepository).findById(investTypeId);
        verify(scenarioRepository, never()).findById(any());
        verifyNoInteractions(distributionService);
        verify(investmentTypeRepository, never()).save(any());
    }

    @Test
    void testUpdateInvestmentType_ScenarioNotFound() {
        // Given: an existing InvestmentType, but the new scenarioId in DTO does not exist
        Long investTypeId = 80L;
        Scenario originalScenario = createScenario(8L);
        InvestmentType existingInvestmentType = InvestmentType.builder()
                .id(investTypeId)
                .scenario(originalScenario)
                .name("Type Y")
                .description("Desc Y")
                .expectedAnnualReturn(null)
                .expenseRatio(0.12)
                .expectedAnnualIncome(null)
                .taxability("Y")
                .build();
        when(investmentTypeRepository.findById(investTypeId)).thenReturn(Optional.of(existingInvestmentType));
        // Update DTO with a non-existent scenarioId
        InvestmentTypeDTO updateDto = new InvestmentTypeDTO();
        updateDto.setScenarioId(999L);
        updateDto.setName("Type Y Updated");
        // Stub scenarioRepository to return empty for the new scenarioId
        when(scenarioRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then: updateInvestmentType should throw exception due to missing scenario
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> investmentTypeService.updateInvestmentType(investTypeId, updateDto));
        assertTrue(exception.getMessage().contains("Scenario not found with id: 999"));
        // Verify interactions: findById on investmentType called, then scenario findById called, then exception thrown (no save or distribution calls)
        verify(investmentTypeRepository).findById(investTypeId);
        verify(scenarioRepository).findById(999L);
        verifyNoInteractions(distributionService);
        verify(investmentTypeRepository, never()).save(any());
    }

    @Test
    void testDeleteInvestmentType_Success() {
        // Given: an existing InvestmentType in the repository
        Long investTypeId = 30L;
        Scenario scenario = createScenario(2L);
        InvestmentType existingInvestmentType = InvestmentType.builder()
                .id(investTypeId)
                .scenario(scenario)
                .name("To Delete")
                .description("To be deleted")
                .expectedAnnualReturn(null)
                .expenseRatio(0.05)
                .expectedAnnualIncome(null)
                .taxability("N")
                .build();
        when(investmentTypeRepository.findById(investTypeId)).thenReturn(Optional.of(existingInvestmentType));
        doNothing().when(investmentTypeRepository).delete(existingInvestmentType);

        // When: calling deleteInvestmentType
        investmentTypeService.deleteInvestmentType(investTypeId);

        // Then: verify that findById and delete were called appropriately
        verify(investmentTypeRepository).findById(investTypeId);
        verify(investmentTypeRepository).delete(same(existingInvestmentType));
    }

    @Test
    void testDeleteInvestmentType_NotFound() {
        // Given: no InvestmentType exists with the given id
        Long investTypeId = 40L;
        when(investmentTypeRepository.findById(investTypeId)).thenReturn(Optional.empty());

        // When & Then: deleteInvestmentType should throw RuntimeException for not found
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> investmentTypeService.deleteInvestmentType(investTypeId));
        assertTrue(exception.getMessage().contains("InvestmentType not found"));
        // Verify that delete was never called
        verify(investmentTypeRepository).findById(investTypeId);
        verify(investmentTypeRepository, never()).delete(any());
    }

    @Test
    void testGetInvestmentTypeList_SuccessWithResults() {
        // Given: a scenarioId and multiple InvestmentType entities associated with it
        Long scenarioId = 5L;
        Scenario scenario = createScenario(scenarioId);
        // Create two InvestmentType entities for the scenario
        DistributionEmbeddable retEmb1 = createDistributionEmbeddable("FIXED", "PERCENT", 5.0);
        DistributionEmbeddable incEmb1 = createDistributionEmbeddable("FIXED", "AMOUNT", 1000.0);
        InvestmentType entity1 = InvestmentType.builder()
                .id(101L)
                .scenario(scenario)
                .name("Investment A")
                .description("Desc A")
                .expectedAnnualReturn(retEmb1)
                .expenseRatio(0.20)
                .expectedAnnualIncome(incEmb1)
                .taxability("Y")
                .build();
        InvestmentType entity2 = InvestmentType.builder()
                .id(102L)
                .scenario(scenario)
                .name("Investment B")
                .description("Desc B")
                .expectedAnnualReturn(null)   // no expectedAnnualReturn
                .expenseRatio(0.30)
                .expectedAnnualIncome(null)   // no expectedAnnualIncome
                .taxability("N")
                .build();
        List<InvestmentType> entityList = Arrays.asList(entity1, entity2);
        when(investmentTypeRepository.findAllByScenarioId(scenarioId)).thenReturn(entityList);
        // Stub DistributionService to convert non-null embeddables to DTOs
        DistributionDTO retDto1 = createDistributionDTO("FIXED", "PERCENT", 5.0);
        DistributionDTO incDto1 = createDistributionDTO("FIXED", "AMOUNT", 1000.0);
        when(distributionService.convertEmbeddableToDTO(retEmb1)).thenReturn(retDto1);
        when(distributionService.convertEmbeddableToDTO(incEmb1)).thenReturn(incDto1);

        // When: calling getInvestmentTypeList
        List<InvestmentTypeDTO> resultList = investmentTypeService.getInvestmentTypeList(scenarioId);

        // Then: verify that repository was called and result list is properly populated
        verify(investmentTypeRepository).findAllByScenarioId(scenarioId);
        // Verify distributionService was called for non-null distributions and not for null ones
        verify(distributionService).convertEmbeddableToDTO(retEmb1);
        verify(distributionService).convertEmbeddableToDTO(incEmb1);
        verifyNoMoreInteractions(distributionService);
        // Assert the size and contents of the returned list
        assertNotNull(resultList);
        assertEquals(2, resultList.size());
        // Validate first DTO corresponds to entity1
        InvestmentTypeDTO dto1 = resultList.get(0);
        assertEquals(entity1.getId(), dto1.getId());
        assertEquals(scenarioId, dto1.getScenarioId(), "ScenarioId in DTO should match input scenarioId");
        assertEquals(entity1.getName(), dto1.getName());
        assertEquals(entity1.getDescription(), dto1.getDescription());
        assertEquals(entity1.getExpenseRatio(), dto1.getExpenseRatio());
        assertEquals(entity1.getTaxability(), dto1.getTaxability());
        assertNotNull(dto1.getExpectedAnnualReturn(), "ExpectedAnnualReturn DTO should be present for entity1");
        assertNotNull(dto1.getExpectedAnnualIncome(), "ExpectedAnnualIncome DTO should be present for entity1");
        assertSame(retDto1, dto1.getExpectedAnnualReturn(), "ExpectedAnnualReturn DTO should come from distributionService");
        assertSame(incDto1, dto1.getExpectedAnnualIncome(), "ExpectedAnnualIncome DTO should come from distributionService");
        // Validate second DTO corresponds to entity2
        InvestmentTypeDTO dto2 = resultList.get(1);
        assertEquals(entity2.getId(), dto2.getId());
        assertEquals(scenarioId, dto2.getScenarioId());
        assertEquals(entity2.getName(), dto2.getName());
        assertEquals(entity2.getDescription(), dto2.getDescription());
        assertEquals(entity2.getExpenseRatio(), dto2.getExpenseRatio());
        assertEquals(entity2.getTaxability(), dto2.getTaxability());
        // expectedAnnualReturn and expectedAnnualIncome should be null for entity2 (since none in entity)
        assertNull(dto2.getExpectedAnnualReturn(), "ExpectedAnnualReturn should be null for entity with none");
        assertNull(dto2.getExpectedAnnualIncome(), "ExpectedAnnualIncome should be null for entity with none");
    }

    @Test
    void testGetInvestmentTypeList_NoResults() {
        // Given: a scenarioId with no associated InvestmentType entities
        Long scenarioId = 10L;
        when(investmentTypeRepository.findAllByScenarioId(scenarioId)).thenReturn(Collections.emptyList());

        // When: calling getInvestmentTypeList
        List<InvestmentTypeDTO> resultList = investmentTypeService.getInvestmentTypeList(scenarioId);

        // Then: the returned list should be empty
        verify(investmentTypeRepository).findAllByScenarioId(scenarioId);
        assertNotNull(resultList);
        assertTrue(resultList.isEmpty(), "Result list should be empty when no investment types found");
        // No distributionService conversions should occur
        verifyNoInteractions(distributionService);
    }
}
