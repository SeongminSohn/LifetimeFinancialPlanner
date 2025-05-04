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
import org.springframework.web.multipart.MultipartFile;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.representer.Representer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
    public List<ScenarioDTO> getScenariosByUserId(Long userId) {
        // Validate the userId
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        // Get All Scenarios and convert to DTO List
        return scenarioRepository.findAll().stream()
                .filter(s -> s.getUser().getId().equals(userId))
                .map(s -> {
                    ScenarioDTO dto = new ScenarioDTO();
                    dto.setUserId(userId);
                    dto.setScenarioId(s.getId());
                    dto.setName(s.getName());
                    dto.setMaritalStatus(s.getMaritalStatus());
                    dto.setBirthYearUser(s.getBirthYearUser());
                    dto.setBirthYearSpouse(s.getBirthYearSpouse());
                    dto.setLifeExpectancyUser(distributionService.convertEmbeddableToDTO(s.getLifeExpectancyUser()));
                    dto.setLifeExpectancySpouse(
                            s.getLifeExpectancySpouse() != null
                                    ? distributionService.convertEmbeddableToDTO(s.getLifeExpectancySpouse())
                                    : null
                    );
                    dto.setFinancialGoal(s.getFinancialGoal());
                    dto.setAfterTaxContributionLimit(s.getAfterTaxContributionLimit());
                    dto.setStateOfResidence(s.getStateOfResidence());
                    dto.setInflationAssumption(distributionService.convertEmbeddableToDTO(s.getInflationAssumption()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ScenarioYamlDTO importScenarioYaml(MultipartFile file, Long userId) throws IOException {
        // Parse the uploaded YAML into ScenarioYamlDTO
        ScenarioYamlDTO yamlDTO = yamlMapper.readValue(file.getInputStream(), ScenarioYamlDTO.class);

        // Build and save base Scenario via ScenarioDTO
        ScenarioDTO scenarioDTO = new ScenarioDTO();
        scenarioDTO.setUserId(userId);
        scenarioDTO.setName(yamlDTO.getName());
        scenarioDTO.setMaritalStatus("couple".equals(yamlDTO.getMaritalStatus()) ? "Y" : "N");
        scenarioDTO.setBirthYearUser(yamlDTO.getBirthYears().get(0));
        scenarioDTO.setBirthYearSpouse(yamlDTO.getBirthYears().size() > 1
                ? yamlDTO.getBirthYears().get(1) : null);
        scenarioDTO.setLifeExpectancyUser(yamlDTO.getLifeExpectancy().get(0));
        scenarioDTO.setLifeExpectancySpouse(yamlDTO.getLifeExpectancy().size() > 1
                ? yamlDTO.getLifeExpectancy().get(1) : null);
        scenarioDTO.setFinancialGoal(yamlDTO.getFinancialGoal());
        scenarioDTO.setAfterTaxContributionLimit(yamlDTO.getAfterTaxContributionLimit());
        scenarioDTO.setStateOfResidence(yamlDTO.getStateOfResidence());
        scenarioDTO.setInflationAssumption(yamlDTO.getInflationAssumption());
        Scenario created = createScenario(scenarioDTO);
        Long newScenarioId = created.getId();

        // InvestmentTypes
        for (InvestmentTypeDTO investmentTypeDTO : yamlDTO.getInvestmentTypes()) {
            investmentTypeDTO.setScenarioId(newScenarioId);
            investmentTypeService.createInvestmentType(investmentTypeDTO);
        }

        // Investments
        for (InvestmentDTO investmentDTO : yamlDTO.getInvestments()) {
            investmentDTO.setScenarioId(newScenarioId);
            investmentService.createInvestment(investmentDTO);
        }

        // EventSeries (Income / Expense / Invest)
        for (EventSeriesDTO eventSeriesDTO : yamlDTO.getEventSeries()) {
            switch (eventSeriesDTO.getType()) {
                case "INCOME":
                    IncomeEventDTO incomeEventDTO = new IncomeEventDTO();
                    incomeEventDTO.setScenarioId(newScenarioId);
                    incomeEventDTO.setName(eventSeriesDTO.getName());
                    incomeEventDTO.setStartYear(eventSeriesDTO.getStart());
                    incomeEventDTO.setDuration(eventSeriesDTO.getDuration());
                    incomeEventDTO.setEventType("INCOME");
                    incomeEventDTO.setInitialAmount(eventSeriesDTO.getInitialAmount());
                    incomeEventDTO.setAnnualChange(eventSeriesDTO.getChangeDistribution());
                    incomeEventDTO.setInflationAdjustment(eventSeriesDTO.getInflationAdjusted() ? "Y" : "N");
                    incomeEventDTO.setUserPercentage(eventSeriesDTO.getUserFraction());
                    incomeEventDTO.setIsSocialSecurity(eventSeriesDTO.getSocialSecurity() ? "Y" : "N");
                    incomeEventService.createIncomeEvent(incomeEventDTO);
                    break;

                case "EXPENSE":
                    ExpenseEventDTO expenseEventDTO = new ExpenseEventDTO();
                    expenseEventDTO.setScenarioId(newScenarioId);
                    expenseEventDTO.setName(eventSeriesDTO.getName());
                    expenseEventDTO.setStartYear(eventSeriesDTO.getStart());
                    expenseEventDTO.setDuration(eventSeriesDTO.getDuration());
                    expenseEventDTO.setEventType("EXPENSE");
                    expenseEventDTO.setInitialAmount(eventSeriesDTO.getInitialAmount());
                    expenseEventDTO.setAnnualChange(eventSeriesDTO.getChangeDistribution());
                    expenseEventDTO.setInflationAdjustment(eventSeriesDTO.getInflationAdjusted() ? "Y" : "N");
                    expenseEventDTO.setUserPercentage(eventSeriesDTO.getUserFraction());
                    expenseEventDTO.setIsDiscretionary(eventSeriesDTO.getDiscretionary() ? "Y" : "N");
                    expenseEventService.createExpenseEvent(expenseEventDTO);
                    break;

                case "INVEST":
                    InvestEventDTO investEventDTO = new InvestEventDTO();
                    investEventDTO.setScenarioId(newScenarioId);
                    investEventDTO.setName(eventSeriesDTO.getName());
                    investEventDTO.setStartYear(eventSeriesDTO.getStart());
                    investEventDTO.setDuration(eventSeriesDTO.getDuration());
                    investEventDTO.setEventType("INVEST");
                    List<AllocationDTO> assetAllocations = eventSeriesDTO.getAssetAllocation().entrySet().stream()
                            .map(e -> {
                                AllocationDTO a = new AllocationDTO();
                                a.setInvestmentKey(e.getKey());
                                a.setRatio(e.getValue());
                                return a;
                            }).collect(Collectors.toList());
                    investEventDTO.setAssetAllocations(assetAllocations);
                    investEventDTO.setMaxCash(eventSeriesDTO.getMaxCash());
                    investEventService.createInvestEvent(investEventDTO);
                    break;
            }
        }

        // ExpenseWithdrawalStrategy
        ExpenseWithdrawalStrategyDTO expenseWithdrawalStrategyDTO = new ExpenseWithdrawalStrategyDTO();
        expenseWithdrawalStrategyDTO.setScenarioId(newScenarioId);
        expenseWithdrawalStrategyDTO.setSellingOrder(yamlDTO.getExpenseWithdrawalStrategy());
        expenseWithdrawalStrategyService.createExpenseWithdrawalStrategy(expenseWithdrawalStrategyDTO);

        // Return the original parsed DTO
        return yamlDTO;
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
        ScenarioYamlDTO yamlDTO = new ScenarioYamlDTO();
        yamlDTO.setName(scenario.getName());
        yamlDTO.setMaritalStatus("Y".equals(scenario.getMaritalStatus()) ? "couple" : "individual");
        yamlDTO.setBirthYears(List.of(scenario.getBirthYearUser(), scenario.getBirthYearSpouse()));
        yamlDTO.setLifeExpectancy(List.of(
                distributionService.convertEmbeddableToDTO(scenario.getLifeExpectancyUser()),
                distributionService.convertEmbeddableToDTO(scenario.getLifeExpectancySpouse())
        ));
        yamlDTO.setInvestmentTypes(investmentTypes);
        yamlDTO.setInvestments(investments);
        yamlDTO.setEventSeries(events);
        yamlDTO.setInflationAssumption(distributionService.convertEmbeddableToDTO(scenario.getInflationAssumption()));
        yamlDTO.setAfterTaxContributionLimit(scenario.getAfterTaxContributionLimit());
        yamlDTO.setExpenseWithdrawalStrategy(withdrawDto.getSellingOrder());
        yamlDTO.setFinancialGoal(scenario.getFinancialGoal());
        yamlDTO.setStateOfResidence(scenario.getStateOfResidence());

        // Citation: GPT helped me how to configure Yaml formatting
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.FLOW);
        options.setPrettyFlow(true);
        options.setDefaultScalarStyle(DumperOptions.ScalarStyle.PLAIN);

        // Create a SnakeYAML instance
        Yaml snake = new Yaml(new Representer(), options);

        // Dump the DTO to a flow-style YAML string
        String yamlString = snake.dump(yamlDTO);

        // Convert to bytes and wrap in Resource
        byte[] yamlBytes = yamlString.getBytes(StandardCharsets.UTF_8);
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
