package com.app.lifetimefinancialplanner.service;

import com.app.lifetimefinancialplanner.domain.context.SimulationContext;
import com.app.lifetimefinancialplanner.domain.dto.SimulationDTO;
import com.app.lifetimefinancialplanner.domain.dto.SimulationYearDTO;
import com.app.lifetimefinancialplanner.domain.entity.InvestEvent;
import com.app.lifetimefinancialplanner.domain.entity.Scenario;
import com.app.lifetimefinancialplanner.domain.entity.Simulation;
import com.app.lifetimefinancialplanner.domain.entity.SimulationYear;
import com.app.lifetimefinancialplanner.repository.InvestEventRepository;
import com.app.lifetimefinancialplanner.repository.ScenarioRepository;
import com.app.lifetimefinancialplanner.repository.SimulationRepository;
import com.app.lifetimefinancialplanner.repository.SimulationYearRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

//for simulation
import org.springframework.scheduling.annotation.Async;
import java.util.concurrent.CompletableFuture;

@Service
public class SimulationServiceImpl implements SimulationService {
    private static final Logger log = LoggerFactory.getLogger(IncomeEventServiceImpl.class);

    private final SimulationRepository simulationRepository;
    private final SimulationYearRepository simulationYearRepository;
    private final InvestEventRepository investEventRepository;
    private final ScenarioRepository scenarioRepository;
    private final IncomeEventService incomeEventService;
    private final ExpenseEventService expenseEventService;
    private final InvestmentService investmentService;
    private final InvestEventService investEventService;
    private final ExpenseWithdrawalStrategyService expenseWithdrawalStrategyService;
    private final SamplingService samplingService;
    private final DistributionService distributionService;
    private final TaxService taxService;
    private final LogService logService;

    public SimulationServiceImpl(SimulationRepository simulationRepository,
                                 SimulationYearRepository simulationYearRepository,
                                 InvestEventRepository investEventRepository,
                                 ScenarioRepository scenarioRepository,
                                 ExpenseWithdrawalStrategyService expenseWithdrawalStrategyService,
                                 IncomeEventService incomeEventService,
                                 ExpenseEventService expenseEventService,
                                 InvestmentService investmentService,
                                 InvestEventService investEventService,
                                 SamplingService samplingService,
                                 DistributionService distributionService,
                                 TaxService taxService,
                                 LogService logService) {
        this.simulationRepository = simulationRepository;
        this.simulationYearRepository = simulationYearRepository;
        this.investEventRepository = investEventRepository;
        this.scenarioRepository = scenarioRepository;
        this.incomeEventService = incomeEventService;
        this.expenseEventService = expenseEventService;
        this.investmentService = investmentService;
        this.investEventService = investEventService;
        this.expenseWithdrawalStrategyService = expenseWithdrawalStrategyService;
        this.samplingService = samplingService;
        this.distributionService = distributionService;
        this.taxService = taxService;
        this.logService = logService;
    }

    @Override
    @Transactional(readOnly = true)
    public SimulationDTO getSimulation(Long simulationId) {
        Simulation sim = simulationRepository.findById(simulationId)
                .orElseThrow(() -> new EntityNotFoundException("Simulation not found: " + simulationId));
        return convertSimulationToDto(sim);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SimulationDTO> getSimulationsByScenario(Long scenarioId) {
        return simulationRepository
                .findByScenarioIdOrderBySimulationCountAsc(scenarioId)
                .stream()
                .map(this::convertSimulationToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SimulationDTO> getSimulationsByBatch(Long batchId) {
        return simulationRepository
                .findAllByBatchIdOrderBySimulationCountAsc(batchId)
                .stream()
                .map(this::convertSimulationToDto)
                .collect(Collectors.toList());
    }

    // Create SimulationDTO and update the values
    private SimulationDTO convertSimulationToDto(Simulation sim) {
        SimulationDTO simulationDTO = new SimulationDTO();
        simulationDTO.setId(sim.getId());
        simulationDTO.setScenarioId(sim.getScenario().getId());
        simulationDTO.setBatchId(sim.getBatchId());
        simulationDTO.setSimulationCount(sim.getSimulationCount());
        simulationDTO.setResult(sim.getResult());
        simulationDTO.setCreatedAt(sim.getCreatedAt());

        List<SimulationYear> years = simulationYearRepository
                .findBySimulationIdOrderBySimulationIndexAsc(sim.getId());
        List<SimulationYearDTO> yearDTOList = years.stream()
                .map(year -> {
                    SimulationYearDTO simulationYearDTO = new SimulationYearDTO();
                    simulationYearDTO.setId(year.getId());
                    simulationYearDTO.setSimulationId(year.getSimulation().getId());
                    simulationYearDTO.setSimulationIndex(year.getSimulationIndex());
                    simulationYearDTO.setYear(year.getYear());
                    simulationYearDTO.setTotalInvestments(year.getTotalInvestments());
                    simulationYearDTO.setTotalIncome(year.getTotalIncome());
                    simulationYearDTO.setTotalExpenses(year.getTotalExpenses());
                    simulationYearDTO.setTotalTax(year.getTotalTax());
                    simulationYearDTO.setCashBalance(year.getCashBalance());
                    simulationYearDTO.setAssetAllocations(year.getAssetAllocations());
                    simulationYearDTO.setCurYearIncome(year.getCurYearIncome());
                    simulationYearDTO.setCurYearSS(year.getCurYearSS());
                    simulationYearDTO.setExpenseBreakdowns(year.getExpenseBreakdowns());
                    simulationYearDTO.setFederalTax(year.getFederalTax());
                    simulationYearDTO.setStateTax(year.getStateTax());
                    simulationYearDTO.setCapitalGainsTax(year.getCapitalGainsTax());
                    simulationYearDTO.setEarlyWithdrawalTax(year.getEarlyWithdrawalTax());
                    simulationYearDTO.setDetails(year.getDetails());
                    simulationYearDTO.setCreatedAt(year.getCreatedAt());
                    return simulationYearDTO;
                })
                .collect(Collectors.toList());
        simulationDTO.setSimulationYears(yearDTOList);

        return simulationDTO;
    }

    private boolean overlapsExisting(Pair<Integer,Integer> candidate, Collection<Pair<Integer,Integer>> scheduled) {
        int cStart = candidate.getLeft();
        int cEnd   = cStart + candidate.getRight();
        for (Pair<Integer,Integer> prev : scheduled) {
            int pStart = prev.getLeft();
            int pEnd   = pStart + prev.getRight();
            if (cStart < pEnd && pStart < cEnd) {
                return true;
            }
        }
        return false;
    }

    @Override
    @Transactional
    public List<SimulationDTO> runSimulation(Long scenarioId, Integer simulationCount) {
        Scenario scenario = scenarioRepository.findById(scenarioId)
                .orElseThrow(() -> new IllegalArgumentException("Scenario not found with id: " + scenarioId));
        List<SimulationDTO> resultList = new ArrayList<>();
        SimulationContext context = new SimulationContext();

        // Citation: GPT helped me to validate the overlap of InvestEvents by using eventSchedule Map
        List<InvestEvent> allInvestEvents = investEventRepository.findAllByEventSeries_Scenario_Id(scenarioId);
        Map<Long, Pair<Integer, Integer>> investEventSchedule = new HashMap<>();
        final int MAX_TRIES = 100;
        for (InvestEvent event : allInvestEvents) {
            int tries = 0;
            Pair<Integer,Integer> sampled;
            // Sample start and duration for the event
            do {
                int sampledStart = (int) samplingService.sample(
                        distributionService.convertEmbeddableToDTO(event.getEventSeries().getStartYear())
                );
                int sampledDur = (int) samplingService.sample(
                        distributionService.convertEmbeddableToDTO(event.getEventSeries().getDuration())
                );
                if (sampledDur < 0) sampledDur = 0;
                sampled = Pair.of(sampledStart, sampledDur);
                tries++;
            }
            // Validate and exclude the events that overlap
            while (overlapsExisting(sampled, investEventSchedule.values()) && tries < MAX_TRIES);

            if (tries >= MAX_TRIES) {
                throw new IllegalStateException(
                        "Unable to schedule InvestEvent with no overlap after " + MAX_TRIES + " tries"
                );
            }
            investEventSchedule.put(event.getEventSeries().getId(), sampled);
        }
        context.setInvestEventSchedule(investEventSchedule);

        // Save the filtered events in the schedule
        List<InvestEvent> scheduledEvents = allInvestEvents.stream()
                .filter(ev -> investEventSchedule.containsKey(ev.getEventSeries().getId()))
                .collect(Collectors.toList());
        context.setUpdatedInvestEvents(scheduledEvents);

        // Create a log file name with username and timestamp
        String userName = scenario.getUser().getName();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String timestamp = LocalDateTime.now().format(formatter);
        String logFilePrefix = userName + "_" + timestamp;

        // Calculate user's current age and sample user's life expectancy
        int startYear = LocalDateTime.now().getYear();
        int currentUserAge = startYear - scenario.getBirthYearUser();
        int userLifeExpectancy = (int) samplingService.sample(distributionService.convertEmbeddableToDTO(scenario.getLifeExpectancyUser()));
        int remainingUserYears = userLifeExpectancy - currentUserAge;
        boolean userAlive = currentUserAge < userLifeExpectancy;

        // If scenario is for married couple, calculate spouse's age and life expectancy.
        int remainingSpouseYears = 0;
        boolean spouseAlive = false;
        if ("Y".equalsIgnoreCase(scenario.getMaritalStatus()) && scenario.getBirthYearSpouse() != null) {
            int currentSpouseAge = startYear - scenario.getBirthYearSpouse();
            int spouseLifeExpectancy = (int) samplingService.sample(distributionService.convertEmbeddableToDTO(scenario.getLifeExpectancySpouse()));
            remainingSpouseYears = spouseLifeExpectancy - currentSpouseAge;
            spouseAlive = currentSpouseAge < spouseLifeExpectancy;
        }

        // Create batchId for Set of simulations
        Long batchId = null;

        for (int runIndex = 1; runIndex <= simulationCount; runIndex++) {
            Simulation simulation;
            if (runIndex == 1) {
                simulation = Simulation.builder()
                        .scenario(scenario)
                        .simulationCount(1)
                        .build();
                simulation = simulationRepository.saveAndFlush(simulation);
                batchId = simulation.getId();
                simulation = simulation.toBuilder()
                        .batchId(batchId)
                        .build();
            } else {
                simulation = Simulation.builder()
                        .scenario(scenario)
                        .simulationCount(runIndex)
                        .batchId(batchId)
                        .build();
            }
            simulation = simulationRepository.save(simulation);

            // Prepare context and DTO list
            List<SimulationYearDTO> simulationYearDTOList = new ArrayList<>();

            // Update numYears by comparing user's and spouse's life expectancy
            int numYears = scenario.getBirthYearSpouse() != null
                    ? Math.max(remainingUserYears, remainingSpouseYears)
                    : remainingUserYears;

            // cumulative inflation rate across simulation
            double cumulativeInflation = 1.0;

            for (int i = 0; i < numYears; i++) {
                int currentYear = startYear + i;

                // Update simulation variables for each year
                context.setCurrentYear(currentYear);

                // Sample the current year's inflation rate and calculate inflation factor.
                double currentInflationRate = samplingService.sample(
                        distributionService.convertEmbeddableToDTO(scenario.getInflationAssumption())
                );
                context.setCurrentInflationRate(currentInflationRate);
                double inflationFactor = 1 + currentInflationRate;
                context.setInflationFactor(inflationFactor);

                // Update cumulative inflation.
                cumulativeInflation *= inflationFactor;
                context.setCumulativeInflation(cumulativeInflation);

                // Adjust retirement contribution limit.
                double adjustedAfterTaxLimit = scenario.getAfterTaxContributionLimit() * inflationFactor;
                context.setAdjustedAfterTaxContributionLimit(adjustedAfterTaxLimit);

                if (i > 0) {
                    // Save current year results as previous year results for next year's iteration
                    context.setPrevYearIncome(context.getCurYearIncome());
                    context.setPrevYearSS(context.getCurYearSS());
                    context.setPrevYearGains(context.getCurYearGains());
                    context.setPrevYearEarlyWithdrawals(context.getCurYearEarlyWithdrawals());
                    context.setPrevTotalExpenses(context.getTotalExpenses());
                    context.setPrevTotalTax(context.getTotalTax());
                    context.setPrevCashBalance(context.getCashBalance());
                }

                // At the beginning of each simulation year, reset per-year fields in SimulationContext
                context.setCurYearIncome(BigDecimal.ZERO);
                context.setCurYearSS(BigDecimal.ZERO);
                context.setCurYearGains(BigDecimal.ZERO);
                context.setCurYearEarlyWithdrawals(BigDecimal.ZERO);
                context.setTotalExpenses(BigDecimal.ZERO);
                context.setTotalTax(BigDecimal.ZERO);
                context.setAssetAllocations(new ArrayList<>());
                context.getExpenseBreakdowns().clear();
                context.setFederalTax(BigDecimal.ZERO);
                context.setStateTax(BigDecimal.ZERO);
                context.setCapitalGainsTax(BigDecimal.ZERO);
                context.setEarlyWithdrawalTax(BigDecimal.ZERO);

                /* --- Begin simulation for the current year ---
                 * The results from these events should update local variables for SimulationYear
                 * such as: totalInvestments, totalIncome, totalExpenses, totalTax, cashBalance.
                 */
                incomeEventService.runIncomeEvents(scenario, context, userAlive, spouseAlive);
                investmentService.updateInvestmentValues(scenario, context);
                payExpenseAndTax(scenario, context, userAlive, spouseAlive);
                investEventService.runInvestEvents(scenario, context);

                // Update totalInvestments to reflect post-contribution values at end of year
                BigDecimal sumInvestments = context.getUpdatedInvestments().stream()
                        .map(inv -> BigDecimal.valueOf(inv.getValue()))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                context.setTotalInvestments(sumInvestments);


                String details = "Year " + currentYear + " processed with inflation factor " + cumulativeInflation;
                context.setDetails(details);
                context.setTimestamp(LocalDateTime.now());

                // Build and save SimulationYear entity
                SimulationYear simulationYear = SimulationYear.builder()
                        .simulation(simulation)
                        .simulationIndex(i + 1)
                        .year(currentYear)
                        .totalInvestments(context.getTotalInvestments())
                        .totalIncome(context.getCurYearIncome())
                        .totalExpenses(context.getTotalExpenses())
                        .totalTax(context.getTotalTax())
                        .cashBalance(context.getCashBalance())
                        .assetAllocations(context.getAssetAllocations())
                        .curYearIncome(context.getCurYearIncome())
                        .curYearSS(context.getCurYearSS())
                        .expenseBreakdowns(context.getExpenseBreakdowns())
                        .federalTax(context.getFederalTax())
                        .stateTax(context.getStateTax())
                        .capitalGainsTax(context.getCapitalGainsTax())
                        .earlyWithdrawalTax(context.getEarlyWithdrawalTax())
                        .details(context.getDetails())
                        .build();
                simulationYear = simulationYearRepository.save(simulationYear);

                // Convert entity to DTO
                SimulationYearDTO yearDTO = new SimulationYearDTO();
                yearDTO.setId(simulationYear.getId());
                yearDTO.setSimulationId(simulationYear.getId());
                yearDTO.setSimulationIndex(i + 1);
                yearDTO.setYear(simulationYear.getYear());
                yearDTO.setTotalInvestments(simulationYear.getTotalInvestments());
                yearDTO.setTotalIncome(simulationYear.getTotalIncome());
                yearDTO.setTotalExpenses(simulationYear.getTotalExpenses());
                yearDTO.setTotalTax(simulationYear.getTotalTax());
                yearDTO.setCashBalance(simulationYear.getCashBalance());
                yearDTO.setAssetAllocations(simulationYear.getAssetAllocations());
                yearDTO.setCurYearIncome(simulationYear.getCurYearIncome());
                yearDTO.setCurYearSS(simulationYear.getCurYearSS());
                yearDTO.setExpenseBreakdowns(simulationYear.getExpenseBreakdowns());
                yearDTO.setFederalTax(simulationYear.getFederalTax());
                yearDTO.setStateTax(simulationYear.getStateTax());
                yearDTO.setCapitalGainsTax(simulationYear.getCapitalGainsTax());
                yearDTO.setEarlyWithdrawalTax(simulationYear.getEarlyWithdrawalTax());
                yearDTO.setDetails(simulationYear.getDetails());
                yearDTO.setCreatedAt(simulationYear.getCreatedAt());

                simulationYearDTOList.add(yearDTO);

                // Log a text entry for the current simulation year.
                logService.writeTextLog(logFilePrefix + ".log", "Full SimulationContext: " + context.toString());
            }

            // Update the simulation result and save to database
            SimulationYearDTO lastYearDTO = simulationYearDTOList.get(simulationYearDTOList.size() - 1);
            BigDecimal endingNetWorth = lastYearDTO.getTotalInvestments().add(lastYearDTO.getCashBalance());
            BigDecimal financialGoal = BigDecimal.valueOf(scenario.getFinancialGoal());
            String resultValue = endingNetWorth.compareTo(financialGoal) >= 0 ? "SUCCESS" : "FAIL";

            simulation = simulation.toBuilder()
                    .result(resultValue)
                    .build();
            simulation = simulationRepository.save(simulation);

            // CSV log data: first row is the header, followed by row data for each simulation year.
            List<String> csvRows = new ArrayList<>();
            String header = "Year,TotalInvestments,TotalIncome,TotalExpenses,TaxesPaid,CashBalance";
            for (SimulationYearDTO simulationYearDTO : simulationYearDTOList) {
                String row = simulationYearDTO.getYear() + "," + simulationYearDTO.getTotalInvestments() + "," +
                        simulationYearDTO.getTotalIncome() + "," + simulationYearDTO.getTotalExpenses() + "," +
                        simulationYearDTO.getTotalTax() + "," + simulationYearDTO.getCashBalance();
                csvRows.add(row);
            }
            logService.writeCsvLog(logFilePrefix + ".csv", header, csvRows);

            // Build SimulationDTO
            SimulationDTO simulationDTO = new SimulationDTO();
            simulationDTO.setId(simulation.getId());
            simulationDTO.setScenarioId(simulation.getScenario().getId());
            simulationDTO.setSimulationCount(simulation.getSimulationCount());
            simulationDTO.setBatchId(simulation.getBatchId());
            simulationDTO.setResult(simulation.getResult());
            simulationDTO.setCreatedAt(simulation.getCreatedAt());
            simulationDTO.setSimulationYears(simulationYearDTOList);

            resultList.add(simulationDTO);
        }
        return resultList;
    }

    @Override
    @Transactional
    public void payExpenseAndTax(Scenario scenario, SimulationContext context, Boolean userAlive, Boolean spouseAlive) {
        // Determine filing status
        String filingStatus = (userAlive && spouseAlive) ? "MARRIED_JOINT" : "SINGLE";
        int currentYear = context.getCurrentYear();

        // Previous year's tax calculations
        BigDecimal taxableIncome;
        BigDecimal federalTax = BigDecimal.ZERO;
        BigDecimal stateTax = BigDecimal.ZERO;
        BigDecimal capitalGainsTax = BigDecimal.ZERO;
        BigDecimal earlyWithdrawalTax = BigDecimal.ZERO;

        if (currentYear == LocalDateTime.now().getYear()) {
            // First simulation year: no tax
            taxableIncome = BigDecimal.ZERO;
        }
        else {
            taxableIncome = context.getPrevYearIncome().add(
                    context.getPrevYearSS().multiply(BigDecimal.valueOf(0.85))
            );
            if (taxableIncome.compareTo(BigDecimal.ZERO) < 0) {
                taxableIncome = BigDecimal.ZERO;
            }

            federalTax = taxService.calculateFederalTax(taxableIncome, filingStatus);
            stateTax = taxService.calculateStateTax(taxableIncome, scenario.getStateOfResidence(), filingStatus);
            capitalGainsTax = taxService.calculateCapitalGainsTax(context.getPrevYearGains(), filingStatus, scenario.getStateOfResidence());

            int userAge = currentYear - scenario.getBirthYearUser();
            if (userAge < 59) {
                earlyWithdrawalTax = taxService.calculateEarlyWithdrawalTax(context.getPrevYearEarlyWithdrawals());
            }
        }
        context.setFederalTax(federalTax);
        context.setStateTax(stateTax);
        context.setCapitalGainsTax(capitalGainsTax);
        context.setEarlyWithdrawalTax(earlyWithdrawalTax);

        // Sum up total tax
        BigDecimal totalTax = federalTax.add(stateTax).add(capitalGainsTax).add(earlyWithdrawalTax);
        context.setTotalTax(totalTax);

        // Compute total payment required: non-discretionary expenses + previous year's taxes
        BigDecimal nonDiscretionaryExpense = expenseEventService.calculateNonDiscretionaryExpense(scenario, context);
        BigDecimal totalPayment = nonDiscretionaryExpense.add(totalTax);

        // Compute withdrawal needed: W = totalPayment - current cash balance
        BigDecimal availableCash = context.getCashBalance();
        BigDecimal withdrawalNeeded = totalPayment.subtract(availableCash);
        if (withdrawalNeeded.compareTo(BigDecimal.ZERO) < 0) {
            withdrawalNeeded = BigDecimal.ZERO;
        }

        expenseWithdrawalStrategyService.withdrawFundsForExpenses(scenario, context, withdrawalNeeded);

        // Deduct the total payment from the cash balance.
        context.setCashBalance(context.getCashBalance().subtract(totalPayment));
        // After paying expenses and tax, add current year income to cash balance.
        context.setCashBalance(context.getCashBalance().add(context.getCurYearIncome()).add(context.getCurYearSS()));
    }
}
