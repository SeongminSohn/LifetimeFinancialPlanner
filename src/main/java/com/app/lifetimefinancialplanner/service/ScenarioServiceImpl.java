package com.app.lifetimefinancialplanner.service;

import com.app.lifetimefinancialplanner.domain.dto.*;
import com.app.lifetimefinancialplanner.domain.embeddable.DistributionEmbeddable;
import com.app.lifetimefinancialplanner.domain.entity.ExpenseWithdrawalStrategy;
import com.app.lifetimefinancialplanner.domain.entity.Scenario;
import com.app.lifetimefinancialplanner.domain.entity.User;
import com.app.lifetimefinancialplanner.repository.ScenarioRepository;
import com.app.lifetimefinancialplanner.repository.UserRepository;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScenarioServiceImpl implements ScenarioService {
    private final ScenarioRepository scenarioRepository;
    private final UserRepository userRepository;
    private final DistributionService distributionService;
    private final InvestmentTypeService investmentTypeService;
    private final InvestmentService investmentService;
    private final ExpenseWithdrawalStrategyService expenseWithdrawalStrategyService;
    private final IncomeEventService incomeEventService;
    private final ExpenseEventService expenseEventService;
    private final InvestEventService investEventService;
    private final YAMLMapper yamlMapper = new YAMLMapper();

    public ScenarioServiceImpl(ScenarioRepository scenarioRepository,
                               UserRepository userRepository,
                               DistributionService distributionService,
                               InvestmentTypeService investmentTypeService,
                               InvestmentService investmentService,
                               ExpenseWithdrawalStrategyService expenseWithdrawalStrategyService,
                               IncomeEventService incomeEventService,
                               ExpenseEventService expenseEventService,
                               InvestEventService investEventService
    ) {
        this.scenarioRepository = scenarioRepository;
        this.userRepository = userRepository;
        this.distributionService = distributionService;
        this.investmentTypeService = investmentTypeService;
        this.investmentService = investmentService;
        this.expenseWithdrawalStrategyService = expenseWithdrawalStrategyService;
        this.incomeEventService = incomeEventService;
        this.expenseEventService = expenseEventService;
        this.investEventService = investEventService;
    }

    @Override
    public Scenario createScenario(ScenarioDTO scenarioDTO) {
        // Retrieve the currently logged-in user
        if (scenarioDTO.getUserId() == null) {
            throw new RuntimeException("User not logged in");
        }

        // Use default contribution limits if null
        Double defaultAfterTax = scenarioDTO.getAfterTaxContributionLimit() != null
                ? scenarioDTO.getAfterTaxContributionLimit() : 7000.0;

        // Convert DTO to Embeddable by using helper method
        DistributionEmbeddable lifeExpUser = distributionService.convertDTOToEmbeddable(scenarioDTO.getLifeExpectancyUser());

        DistributionEmbeddable lifeExpSpouse = null;
        Integer birthYearSpouse = null;
        if (!"N".equalsIgnoreCase(scenarioDTO.getMaritalStatus())) {
            lifeExpSpouse = distributionService.convertDTOToEmbeddable(scenarioDTO.getLifeExpectancySpouse());
            birthYearSpouse = scenarioDTO.getBirthYearSpouse();
        }

        DistributionEmbeddable inflationAssumptionEmb = distributionService.convertDTOToEmbeddable(scenarioDTO.getInflationAssumption());

        User user = userRepository.findById(scenarioDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Build a new Scenario using builder pattern
        Scenario scenario = Scenario.builder()
                .user(user)
                .name(scenarioDTO.getName())
                .maritalStatus(scenarioDTO.getMaritalStatus())
                .birthYearUser(scenarioDTO.getBirthYearUser())
                .birthYearSpouse(birthYearSpouse)
                .lifeExpectancyUser(lifeExpUser)
                .lifeExpectancySpouse(lifeExpSpouse)
                .financialGoal(scenarioDTO.getFinancialGoal())
                .afterTaxContributionLimit(defaultAfterTax)
                .stateOfResidence(scenarioDTO.getStateOfResidence())
                .inflationAssumption(inflationAssumptionEmb)
                .createdAt(LocalDateTime.now())
                .build();

        // Save and return the new scenario
        return scenarioRepository.save(scenario);
    }

    @Override
    public Scenario getScenario(Long id) {
        return scenarioRepository.findById(id).orElse(null);
    }

    @Override
    public Scenario updateScenario(Long scenarioId, ScenarioDTO scenarioDTO) {
        // Check if scenario exists
        Scenario scenario = scenarioRepository.findById(scenarioId)
                .orElseThrow(() -> new RuntimeException("Scenario not found"));

        // Update life expectancy for user if provided
        DistributionEmbeddable updatedLifeExpUser = scenario.getLifeExpectancyUser();
        if (scenarioDTO.getLifeExpectancyUser() != null) {
            updatedLifeExpUser = distributionService.convertDTOToEmbeddable(scenarioDTO.getLifeExpectancyUser());
        }

        DistributionEmbeddable updatedLifeExpSpouse = scenario.getLifeExpectancySpouse();
        if (scenarioDTO.getLifeExpectancySpouse() != null) {
            updatedLifeExpSpouse = distributionService.convertDTOToEmbeddable(scenarioDTO.getLifeExpectancySpouse());
        }

        DistributionEmbeddable updatedInflationAssumption = scenario.getInflationAssumption();
        if (scenarioDTO.getInflationAssumption() != null) {
            updatedInflationAssumption = distributionService.convertDTOToEmbeddable(scenarioDTO.getInflationAssumption());
        }

        // Update Scenario
        Scenario updatedScenario = scenario.toBuilder()
                .name(scenarioDTO.getName() != null ? scenarioDTO.getName() : scenario.getName())
                .maritalStatus(scenarioDTO.getMaritalStatus() != null ? scenarioDTO.getMaritalStatus() : scenario.getMaritalStatus())
                .birthYearUser(scenarioDTO.getBirthYearUser() != null ? scenarioDTO.getBirthYearUser() : scenario.getBirthYearUser())
                .birthYearSpouse(scenarioDTO.getBirthYearSpouse() != null ? scenarioDTO.getBirthYearSpouse() : scenario.getBirthYearSpouse())
                .lifeExpectancyUser(updatedLifeExpUser)
                .lifeExpectancySpouse(updatedLifeExpSpouse)
                .financialGoal(scenarioDTO.getFinancialGoal() != null ? scenarioDTO.getFinancialGoal() : scenario.getFinancialGoal())
                .afterTaxContributionLimit(scenarioDTO.getAfterTaxContributionLimit() != null ? scenarioDTO.getAfterTaxContributionLimit() : scenario.getAfterTaxContributionLimit())
                .stateOfResidence(scenarioDTO.getStateOfResidence() != null ? scenarioDTO.getStateOfResidence() : scenario.getStateOfResidence())
                .inflationAssumption(updatedInflationAssumption)
                .build();

        return scenarioRepository.save(updatedScenario);
    }


    @Override
    public void deleteScenario(Long scenarioId) {
        // Check if scenario exists
        Scenario scenario = scenarioRepository.findById(scenarioId)
                .orElseThrow(() -> new RuntimeException("Scenario not found for deletion"));

        scenarioRepository.delete(scenario);
    }

    @Override
    @Transactional(readOnly = true)
    public Resource exportScenarioYaml(Long scenarioId) throws IOException {
        Scenario scenario = scenarioRepository.findById(scenarioId)
                .orElseThrow(() -> new IllegalArgumentException("Scenario not found: " + scenarioId));

        // Retrieve related DTO lists
        List<InvestmentTypeDTO> investmentTypes = investmentTypeService.getInvestmentTypeList(scenarioId);
        List<InvestmentDTO> investments = investmentService.getInvestmentListByScenarioId(scenarioId);

        ExpenseWithdrawalStrategy strategy = expenseWithdrawalStrategyService.getExpenseWithdrawalStrategyByScenarioId(scenarioId);
        ExpenseWithdrawalStrategyDTO withdrawDto = new ExpenseWithdrawalStrategyDTO();
        withdrawDto.setScenarioId(strategy.getScenarioId());
        withdrawDto.setSellingOrder(strategy.getSellingOrder());

        // Build EventSeriesDTO list
        List<EventSeriesDTO> events = new ArrayList<>();

        // income events
        List<IncomeEventDTO> incomeEventDTOList =
                incomeEventService.getIncomeEventListByScenarioId(scenarioId);
        for (IncomeEventDTO dto : incomeEventDTOList) {
            events.add(mapIncomeSeries(dto));
        }

        // expense events
        List<ExpenseEventDTO> expenseEventDTOList =
                expenseEventService.getExpenseEventsByScenarioId(scenarioId);
        for (ExpenseEventDTO dto : expenseEventDTOList) {
            events.add(mapExpenseSeries(dto));
        }

        // invest events
        List<InvestEventDTO> investEventDTOList =
                investEventService.getInvestEventsByScenarioId(scenarioId);
        for (InvestEventDTO dto : investEventDTOList) {
            events.add(mapInvestSeries(dto));
        }

        // Create ScenarioYamlDTO
        ScenarioYamlDTO yamlDto = new ScenarioYamlDTO();
        yamlDto.setName(scenario.getName());
        yamlDto.setMaritalStatus("Y".equals(scenario.getMaritalStatus()) ? "couple" : "individual");
        yamlDto.setBirthYears(List.of(scenario.getBirthYearUser(), scenario.getBirthYearSpouse()));
        yamlDto.setLifeExpectancy(List.of(
                distributionService.convertEmbeddableToDTO(scenario.getLifeExpectancyUser()),
                distributionService.convertEmbeddableToDTO(scenario.getLifeExpectancySpouse())
        ));
        yamlDto.setInvestmentTypes(investmentTypes);
        yamlDto.setInvestments(investments);
        yamlDto.setEventSeries(events);
        yamlDto.setInflationAssumption(distributionService.convertEmbeddableToDTO(scenario.getInflationAssumption()));
        yamlDto.setAfterTaxContributionLimit(scenario.getAfterTaxContributionLimit());
        yamlDto.setExpenseWithdrawalStrategy(withdrawDto.getSellingOrder());
        yamlDto.setFinancialGoal(scenario.getFinancialGoal());
        yamlDto.setStateOfResidence(scenario.getStateOfResidence());

        // 5) Serialize to YAML and wrap in Resource
        byte[] yamlBytes = yamlMapper.writeValueAsBytes(yamlDto);
        return new ByteArrayResource(yamlBytes);
    }

    // Helper to map IncomeEventDTO → EventSeriesDTO
    private EventSeriesDTO mapIncomeSeries(IncomeEventDTO dto) {
        EventSeriesDTO ev = new EventSeriesDTO();
        ev.setName(dto.getName());
        ev.setStart(dto.getStartYear());
        ev.setDuration(dto.getDuration());
        ev.setType("INCOME");
        ev.setInitialAmount(dto.getInitialAmount());
        ev.setChangeDistribution(dto.getAnnualChange());
        ev.setChangeAmtOrPct(dto.getAnnualChange().getAmountOrPercent());
        ev.setInflationAdjusted("Y".equals(dto.getInflationAdjustment()));
        ev.setUserFraction(dto.getUserPercentage());
        ev.setSocialSecurity("Y".equals(dto.getIsSocialSecurity()));
        return ev;
    }

    // Helper to map ExpenseEventDTO → EventSeriesDTO
    private EventSeriesDTO mapExpenseSeries(ExpenseEventDTO dto) {
        EventSeriesDTO ev = new EventSeriesDTO();
        ev.setName(dto.getName());
        ev.setStart(dto.getStartYear());
        ev.setDuration(dto.getDuration());
        ev.setType("EXPENSE");
        ev.setInitialAmount(dto.getInitialAmount());
        ev.setChangeDistribution(dto.getAnnualChange());
        ev.setChangeAmtOrPct(dto.getAnnualChange().getAmountOrPercent());
        ev.setInflationAdjusted("Y".equals(dto.getInflationAdjustment()));
        ev.setUserFraction(dto.getUserPercentage());
        ev.setDiscretionary("Y".equals(dto.getIsDiscretionary()));
        return ev;
    }


    // Helper to map InvestEventDTO → EventSeriesDTO
    private EventSeriesDTO mapInvestSeries(InvestEventDTO dto) {
        EventSeriesDTO ev = new EventSeriesDTO();
        ev.setName(dto.getName());
        ev.setStart(dto.getStartYear());
        ev.setDuration(dto.getDuration());
        ev.setType("INVEST");
        ev.setAssetAllocation(dto.getAssetAllocations().stream()
                .collect(Collectors.toMap(AllocationDTO::getInvestmentKey, AllocationDTO::getRatio)));
        ev.setMaxCash(dto.getMaxCash());
        return ev;
    }
}
