package com.app.lifetimefinancialplanner.service;

import com.app.lifetimefinancialplanner.domain.dto.*;
import com.app.lifetimefinancialplanner.domain.embeddable.DistributionEmbeddable;
import com.app.lifetimefinancialplanner.domain.entity.ExpenseWithdrawalStrategy;
import com.app.lifetimefinancialplanner.domain.entity.InvestmentType;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
        List<IncomeEventDTO> incomes   = incomeEventService.getIncomeEventListByScenarioId(scenarioId);
        List<ExpenseEventDTO> expenses = expenseEventService.getExpenseEventsByScenarioId(scenarioId);
        List<InvestEventDTO> invests   = investEventService.getInvestEventsByScenarioId(scenarioId);
        ExpenseWithdrawalStrategy strategy = expenseWithdrawalStrategyService.getExpenseWithdrawalStrategyByScenarioId(scenarioId);

        // Build root map
        Map<String,Object> root = new LinkedHashMap<>();
        root.put("name", scenario.getName());
        root.put("maritalStatus", "Y".equalsIgnoreCase(scenario.getMaritalStatus()) ? "couple" : "individual");

        List<Integer> birthYears = new ArrayList<>();
        birthYears.add(scenario.getBirthYearUser());
        if (scenario.getBirthYearSpouse() != null) birthYears.add(scenario.getBirthYearSpouse());
        root.put("birthYears", birthYears);

        List<DistributionDTO> lifeExpectancy = new ArrayList<>();
        lifeExpectancy.add(distributionService.convertEmbeddableToDTO(scenario.getLifeExpectancyUser()));
        if (scenario.getLifeExpectancySpouse() != null)
            lifeExpectancy.add(distributionService.convertEmbeddableToDTO(scenario.getLifeExpectancySpouse()));
        root.put("lifeExpectancy", lifeExpectancy);

        // --- investmentTypes ---
        List<InvestmentTypeDTO> invTypeDtos = investmentTypeService.getInvestmentTypeList(scenarioId);
        List<Map<String,Object>> invTypes = invTypeDtos.stream()
                .map(this::mapInvestmentType)
                .collect(Collectors.toList());
        root.put("investmentTypes", invTypes);

        // --- investments ---
        List<InvestmentDTO> invDtos = investmentService.getInvestmentListByScenarioId(scenarioId);
        List<Map<String,Object>> invMaps = invDtos.stream()
                .map(this::mapInvestment)
                .collect(Collectors.toList());
        root.put("investments", invMaps);

        // eventSeries
        List<Map<String,Object>> events = new ArrayList<>();
        for (IncomeEventDTO dto : incomes)   events.add(mapIncomeSeries(dto));
        for (ExpenseEventDTO dto : expenses) events.add(mapExpenseSeries(dto));
        for (InvestEventDTO dto : invests)   events.add(mapInvestSeries(dto));
        root.put("eventSeries", events);

        root.put("inflationAssumption", mapDistribution(distributionService.convertEmbeddableToDTO(scenario.getInflationAssumption())));
        root.put("afterTaxContributionLimit", scenario.getAfterTaxContributionLimit());
        root.put("expenseWithdrawalStrategy", strategy.getSellingOrder());
        root.put("financialGoal", scenario.getFinancialGoal());
        root.put("residenceState", scenario.getStateOfResidence());

        // Configure YAML flow style
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.FLOW);
        options.setPrettyFlow(true);
        options.setDefaultScalarStyle(DumperOptions.ScalarStyle.PLAIN);

        Yaml snake = new Yaml(new Representer(), options);
        String yamlString = snake.dumpAsMap(root);

        byte[] yamlBytes = yamlString.getBytes(StandardCharsets.UTF_8);
        return new ByteArrayResource(yamlBytes);
    }

    private Map<String,Object> mapInvestmentType(InvestmentTypeDTO dto) {
        Map<String,Object> m = new LinkedHashMap<>();
        m.put("name", dto.getName());
        m.put("description", dto.getDescription());
        m.put("returnAmtOrPct", dto.getExpectedAnnualReturn().getAmountOrPercent().toLowerCase());
        m.put("returnDistribution", mapDistribution(dto.getExpectedAnnualReturn()));
        m.put("expenseRatio", dto.getExpenseRatio());
        m.put("incomeAmtOrPct", dto.getExpectedAnnualIncome().getAmountOrPercent().toLowerCase());
        m.put("incomeDistribution", mapDistribution(dto.getExpectedAnnualIncome()));
        m.put("taxability", "Y".equalsIgnoreCase(dto.getTaxability()));
        return m;
    }

    private Map<String,Object> mapInvestment(InvestmentDTO dto) {
        Map<String,Object> m = new LinkedHashMap<>();

        InvestmentType investmentType = investmentTypeService
                .getInvestmentType(dto.getInvestmentTypeId())
                .orElseThrow(() -> new RuntimeException(
                        "InvestmentType not found: " + dto.getInvestmentTypeId()));

        String typeName = investmentType.getName();
        m.put("investmentType", typeName);
        m.put("value", dto.getValue().intValue());
        m.put("taxStatus", dto.getTaxStatus().toLowerCase());

        // build the “id” field exactly as in the professor’s format
        String idKey = typeName
                + (dto.getTaxStatus().equalsIgnoreCase("non-retirement")
                ? ""
                : " " + dto.getTaxStatus().toLowerCase());
        m.put("id", idKey.trim());

        return m;
    }

    private Map<String,Object> mapDistribution(DistributionDTO d) {
        Map<String,Object> m = new LinkedHashMap<>();
        String type = d.getDistributionType().toLowerCase();  // "fixed","uniform","normal"
        m.put("type", type);
        switch(type) {
            case "fixed":
                m.put("value", d.getValue().intValue());
                break;
            case "uniform":
                m.put("lower", d.getLower().intValue());
                m.put("upper", d.getUpper().intValue());
                break;
            case "normal":
                m.put("mean", d.getMean());
                m.put("stDev", d.getStDev());
                break;
        }
        return m;
    }

    private Map<String,Object> mapIncomeSeries(IncomeEventDTO dto) {
        Map<String,Object> m = new LinkedHashMap<>();
        m.put("name", dto.getName());
        m.put("start", mapDistribution(dto.getStartYear()));
        m.put("duration", mapDistribution(dto.getDuration()));
        m.put("type", "income");
        m.put("initialAmount", dto.getInitialAmount().intValue());
        m.put("changeAmtOrPct", dto.getAnnualChange().getAmountOrPercent().toLowerCase());
        m.put("changeDistribution", mapDistribution(dto.getAnnualChange()));
        m.put("inflationAdjusted", "Y".equals(dto.getInflationAdjustment()));
        m.put("userFraction", dto.getUserPercentage());
        m.put("socialSecurity", "Y".equals(dto.getIsSocialSecurity()));
        return m;
    }

    private Map<String,Object> mapExpenseSeries(ExpenseEventDTO dto) {
        Map<String,Object> m = new LinkedHashMap<>();
        m.put("name", dto.getName());
        m.put("start", mapDistribution(dto.getStartYear()));
        m.put("duration", mapDistribution(dto.getDuration()));
        m.put("type", "expense");
        m.put("initialAmount", dto.getInitialAmount().intValue());
        m.put("changeAmtOrPct", dto.getAnnualChange().getAmountOrPercent().toLowerCase());
        m.put("changeDistribution", mapDistribution(dto.getAnnualChange()));
        m.put("inflationAdjusted", "Y".equals(dto.getInflationAdjustment()));
        m.put("userFraction", dto.getUserPercentage());
        m.put("discretionary", "Y".equals(dto.getIsDiscretionary()));
        return m;
    }

    private Map<String,Object> mapInvestSeries(InvestEventDTO dto) {
        Map<String,Object> m = new LinkedHashMap<>();
        m.put("name", dto.getName());
        m.put("start", mapDistribution(dto.getStartYear()));
        m.put("duration", mapDistribution(dto.getDuration()));
        m.put("type", "invest");
        m.put("assetAllocation", dto.getAssetAllocations().stream()
                .collect(Collectors.toMap(AllocationDTO::getInvestmentKey, AllocationDTO::getRatio,
                        (a,b)->a, LinkedHashMap::new))
        );
        m.put("maxCash", dto.getMaxCash().intValue());
        return m;
    }


}
