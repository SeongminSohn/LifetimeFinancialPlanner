package com.app.lifetimefinancialplanner.service;

import com.app.lifetimefinancialplanner.domain.context.SimulationContext;
import com.app.lifetimefinancialplanner.domain.dto.SimulationDTO;
import com.app.lifetimefinancialplanner.domain.dto.SimulationYearDTO;
import com.app.lifetimefinancialplanner.domain.entity.Scenario;
import com.app.lifetimefinancialplanner.domain.entity.Simulation;
import com.app.lifetimefinancialplanner.domain.entity.SimulationYear;
import com.app.lifetimefinancialplanner.repository.ScenarioRepository;
import com.app.lifetimefinancialplanner.repository.SimulationRepository;
import com.app.lifetimefinancialplanner.repository.SimulationYearRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class SimulationServiceImpl implements SimulationService {

    private final SimulationRepository simulationRepository;
    private final SimulationYearRepository simulationYearRepository;
    private final ScenarioRepository scenarioRepository;
    private final LogService logService;
    private final IncomeEventService incomeEventService;
    private final SamplingService samplingService;
    private final DistributionService distributionService;

    public SimulationServiceImpl(SimulationRepository simulationRepository,
                                 SimulationYearRepository simulationYearRepository,
                                 ScenarioRepository scenarioRepository,
                                 LogService logService,
                                 IncomeEventService incomeEventService,
                                 SamplingService samplingService,
                                 DistributionService distributionService) {
        this.simulationRepository = simulationRepository;
        this.simulationYearRepository = simulationYearRepository;
        this.scenarioRepository = scenarioRepository;
        this.logService = logService;
        this.incomeEventService = incomeEventService;
        this.samplingService = samplingService;
        this.distributionService = distributionService;
    }


    @Override
    @Transactional
    public SimulationDTO runSimulation(Long scenarioId, Integer simulationCount) {
        Scenario scenario = scenarioRepository.findById(scenarioId)
                .orElseThrow(() -> new IllegalArgumentException("Scenario not found with id: " + scenarioId));

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

        // Update numYears by comparing user's and spouse's life expectancy
        int numYears = scenario.getBirthYearSpouse() != null
                ? Math.max(remainingUserYears, remainingSpouseYears)
                : remainingUserYears;

        // cumulative inflation rate across simulation
        double cumulativeInflation = 1.0;

        List<SimulationYearDTO> simulationYearDTOList = new ArrayList<>();
        for (int i = 0; i < numYears; i++) {
            int currentYear = startYear + i;

            // Update simulation variables for each year
            SimulationContext context = new SimulationContext();
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

            /* --- Begin simulation for the current year ---
             * TODO: Call various service methods that process events:
             * - updateInvestments()
             * - payExpenseAndTax()
             * - runInvestEvents()
             * The results from these events should update local variables for SimulationYear
             * such as: totalInvestments, totalIncome, totalExpenses, totalTax, cashBalance.
             */
            incomeEventService.runIncomeEvents(scenario, context, userAlive, spouseAlive);

            // Save the results in SimulationYear TODO: Currently Dummy data
            context.setCurYearIncome(BigDecimal.ZERO);
            context.setCurYearSS(BigDecimal.ZERO);
            context.setTotalInvestments(BigDecimal.ZERO);
            context.setTotalExpenses(BigDecimal.ZERO);
            context.setTotalTax(BigDecimal.ZERO);
            context.setCashBalance(BigDecimal.ZERO);
            String details = "Year " + currentYear + " processed with inflation factor " + cumulativeInflation;
            context.setDetails(details);
            context.setTimestamp(LocalDateTime.now());

            // Build and save SimulationYear entity
            SimulationYear simulationYear = SimulationYear.builder()
                    .simulationIndex(i + 1)
                    .year(currentYear)
                    .totalInvestments(context.getTotalInvestments())
                    .totalIncome(context.getCurYearIncome())
                    .totalExpenses(context.getTotalExpenses())
                    .totalTax(context.getTotalTax())
                    .cashBalance(context.getCashBalance())
                    .details(context.getDetails())
                    .build();
            simulationYear = simulationYearRepository.save(simulationYear);

            // Convert entity to DTO
            SimulationYearDTO yearDTO = new SimulationYearDTO();
            yearDTO.setId(simulationYear.getId());
            yearDTO.setYear(simulationYear.getYear());
            yearDTO.setTotalInvestments(simulationYear.getTotalInvestments());
            yearDTO.setTotalIncome(simulationYear.getTotalIncome());
            yearDTO.setTotalExpenses(simulationYear.getTotalExpenses());
            yearDTO.setTotalTax(simulationYear.getTotalTax());
            yearDTO.setCashBalance(simulationYear.getCashBalance());
            yearDTO.setDetails(simulationYear.getDetails());
            yearDTO.setCreatedAt(simulationYear.getCreatedAt());

            simulationYearDTOList.add(yearDTO);

            // Log a text entry for the current simulation year.
            logService.writeTextLog(logFilePrefix + ".log",
                    "Processed simulation year " + currentYear + " with total investments: " + yearDTO.getTotalInvestments());
        }

        Simulation simulation = Simulation.builder()
                .scenario(scenario)
                .simulationCount(simulationCount)
                .result("Simulation completed")
                .build();
        simulation = simulationRepository.save(simulation);

        // CSV log data: first row is the header, followed by row data for each simulation year.
        List<String> csvRows = new ArrayList<>();
        String header = "Year,TotalInvestments,TotalIncome,TotalExpenses,TaxesPaid,CashBalance";
        for (SimulationYearDTO dto : simulationYearDTOList) {
            String row = dto.getYear() + "," + dto.getTotalInvestments() + "," +
                    dto.getTotalIncome() + "," + dto.getTotalExpenses() + "," +
                    dto.getTotalTax() + "," + dto.getCashBalance();
            csvRows.add(row);
        }
        logService.writeCsvLog(logFilePrefix + ".csv", header, csvRows);

        // Build SimulationDTO
        SimulationDTO simulationDTO = new SimulationDTO();
        simulationDTO.setId(simulation.getId());
        simulationDTO.setScenarioId(simulation.getScenario().getId());
        simulationDTO.setSimulationCount(simulation.getSimulationCount());
        simulationDTO.setResult(simulation.getResult());
        simulationDTO.setCreatedAt(simulation.getCreatedAt());
        simulationDTO.setSimulationYears(simulationYearDTOList);

        return simulationDTO;
    }
}
