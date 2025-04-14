package com.app.lifetimefinancialplanner.service;

import com.app.lifetimefinancialplanner.domain.context.SimulationContext;
import com.app.lifetimefinancialplanner.domain.dto.SimulationDTO;
import com.app.lifetimefinancialplanner.domain.dto.SimulationYearDTO;
import com.app.lifetimefinancialplanner.domain.entity.Investment;
import com.app.lifetimefinancialplanner.domain.entity.Scenario;
import com.app.lifetimefinancialplanner.domain.entity.Simulation;
import com.app.lifetimefinancialplanner.domain.entity.SimulationYear;
import com.app.lifetimefinancialplanner.repository.ScenarioRepository;
import com.app.lifetimefinancialplanner.repository.SimulationRepository;
import com.app.lifetimefinancialplanner.repository.SimulationYearRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class SimulationServiceImpl implements SimulationService {

    private final SimulationRepository simulationRepository;
    private final SimulationYearRepository simulationYearRepository;
    private final ScenarioRepository scenarioRepository;
    private final IncomeEventService incomeEventService;
    private final ExpenseWithdrawalStrategyService expenseWithdrawalStrategyService;
    private final SamplingService samplingService;
    private final DistributionService distributionService;
    private final TaxService taxService;
    private final LogService logService;

    public SimulationServiceImpl(SimulationRepository simulationRepository,
                                 SimulationYearRepository simulationYearRepository,
                                 ScenarioRepository scenarioRepository,
                                 ExpenseWithdrawalStrategyService expenseWithdrawalStrategyService,
                                 IncomeEventService incomeEventService,
                                 SamplingService samplingService,
                                 DistributionService distributionService,
                                 TaxService taxService,
                                 LogService logService) {
        this.simulationRepository = simulationRepository;
        this.simulationYearRepository = simulationYearRepository;
        this.scenarioRepository = scenarioRepository;
        this.incomeEventService = incomeEventService;
        this.expenseWithdrawalStrategyService = expenseWithdrawalStrategyService;
        this.samplingService = samplingService;
        this.distributionService = distributionService;
        this.taxService = taxService;
        this.logService = logService;
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
        SimulationContext context = new SimulationContext();

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

            // At the beginning of each simulation year, reset per-year fields in SimulationContext
            context.setCurYearIncome(BigDecimal.ZERO);
            context.setCurYearSS(BigDecimal.ZERO);
            context.setCurYearGains(BigDecimal.ZERO);
            context.setCurYearEarlyWithdrawals(BigDecimal.ZERO);
            context.setTotalExpenses(BigDecimal.ZERO);
            context.setTotalTax(BigDecimal.ZERO);
            context.setCashBalance(BigDecimal.ZERO);

            /* --- Begin simulation for the current year ---
             * TODO: Call various service methods that process events:
             * - updateInvestments()
             * - payExpenseAndTax()
             * - runInvestEvents()
             * The results from these events should update local variables for SimulationYear
             * such as: totalInvestments, totalIncome, totalExpenses, totalTax, cashBalance.
             */
            incomeEventService.runIncomeEvents(scenario, context, userAlive, spouseAlive);

//            payExpenseAndTax(scenario, context, userAlive, spouseAlive);

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

    @Override
    @Transactional
    public void payExpenseAndTax(Scenario scenario, SimulationContext context, Boolean userAlive, Boolean spouseAlive) {
        // Determine filing status
        String filingStatus = (userAlive && spouseAlive) ? "MARRIED_JOINT" : "SINGLE";

        // Calculate taxable income
        BigDecimal curYearIncome = context.getCurYearIncome();
        BigDecimal curYearSS = context.getCurYearSS();
        // Compute taxable income (social security 85% is taxable)
        BigDecimal taxableIncome = curYearIncome.add(curYearSS.multiply(BigDecimal.valueOf(0.85)));
        if (taxableIncome.compareTo(BigDecimal.ZERO) < 0) {
            taxableIncome = BigDecimal.ZERO;
        }

        // Calculate federal and state income tax
        BigDecimal federalTax = taxService.calculateFederalTax(taxableIncome, filingStatus);
        BigDecimal stateTax = taxService.calculateStateTax(taxableIncome, scenario.getStateOfResidence(), filingStatus);

        // Compute capital gains tax:
        // Example: If context contains total capital gains, apply a rate (e.g., 15% for long-term gains)
        // Here we assume context.getTotalGains() exists; modify accordingly if not yet defined.
        BigDecimal capitalGainsTax = BigDecimal.ZERO;
        // Uncomment and adjust when context has total gains:
        // capitalGainsTax = context.getTotalGains().multiply(BigDecimal.valueOf(0.15));

        // Compute early withdrawal tax:
        int currentYear = context.getCurrentYear();
        int userAge = currentYear - scenario.getBirthYearUser();

        // If user's age is below 59, apply 10% penalty on early withdrawals.
        BigDecimal earlyWithdrawalTax = BigDecimal.ZERO;
         if (userAge < 59) {
             earlyWithdrawalTax = context.getCurYearEarlyWithdrawals().multiply(BigDecimal.valueOf(0.10));
         }

        // Sum up total tax
        BigDecimal totalTax = federalTax.add(stateTax).add(capitalGainsTax).add(earlyWithdrawalTax);
        context.setTotalTax(totalTax);

        // Compute total payment required: non-discretionary expenses + previous year's taxes
        BigDecimal nonDiscretionaryExpense = context.getTotalExpenses();
        BigDecimal totalPayment = nonDiscretionaryExpense.add(totalTax);

        // Compute withdrawal needed: W = totalPayment - current cash balance
        BigDecimal availableCash = context.getCashBalance();
        BigDecimal withdrawalNeeded = totalPayment.subtract(availableCash);
        if (withdrawalNeeded.compareTo(BigDecimal.ZERO) < 0) {
            withdrawalNeeded = BigDecimal.ZERO;
        }

        // Withdraw necessary funds to cover expenses
        // Instead of calling expenseWithdrawalService.withdraw, we use a local method withdrawFundsForExpenses.
//        withdrawFundsForExpenses(scenario, context, withdrawalNeeded);

        // 10. Finally, deduct the total payment from the cash balance.
        context.setCashBalance(availableCash.subtract(totalPayment));
    }

//    private void withdrawFundsForExpenses(Scenario scenario, SimulationContext context, BigDecimal withdrawalNeeded) {
//        // Retrieve the list of investments in the expense withdrawal strategy order
//        List<Investment> withdrawalInvestments = getWithdrawalStrategyInvestments(scenario);
//
//        // Iterate through the investments to sell enough funds
//        for (Investment investment : withdrawalInvestments) {
//            // If the required withdrawal amount has been met, exit the loop.
//            if (withdrawalNeeded.compareTo(BigDecimal.ZERO) <= 0) {
//                break;
//            }
//
//            BigDecimal investmentValue = BigDecimal.valueOf(investment.getValue());
//            // Skip if the investment's value is 0 or negative.
//            if (investmentValue.compareTo(BigDecimal.ZERO) <= 0) {
//                continue;
//            }
//
//            // Determine the amount to sell from the current investment
//            BigDecimal sellAmount;
//            if (investmentValue.compareTo(withdrawalNeeded) >= 0) {
//                sellAmount = withdrawalNeeded;
//            } else {
//                sellAmount = investmentValue;
//            }
//
//            // Save the pre-sale investment value for capital gain calculation.
//            BigDecimal preSaleValue = investmentValue;
//
//            // Update the investment's value by subtracting the sell amount.
//            BigDecimal afterSaleValue = investmentValue.subtract(sellAmount);
//            investment.setValue(afterSaleValue);
////            investment.toBuilder()
////                    .aslkdfjlasdkfj
//
//            // Compute the proportional capital gain for the sold portion.
//            BigDecimal purchasePrice = investment.getPurchasePrice(); // 반드시 null이 아니라고 가정
//            BigDecimal capitalGain;
//            if (sellAmount.compareTo(preSaleValue) == 0) {
//                // If selling the entire investment, capital gain is the difference between the old value and the purchase price.
//                capitalGain = preSaleValue.subtract(purchasePrice);
//            } else {
//                // For a partial sale, compute the fraction sold.
//                BigDecimal fraction = sellAmount.divide(preSaleValue, 10, RoundingMode.HALF_UP);
//                // Then, capital gain for the sold portion is the same fraction of the total (old value - purchasePrice).
//                capitalGain = fraction.multiply(preSaleValue.subtract(purchasePrice));
//            }
//
//            // Update the cumulative capital gains (curYearGains) only for NON-RETIREMENT investments.
//            if ("NON-RETIREMENT".equalsIgnoreCase(investment.getTaxStatus())) {
//                context.setTotalGains(context.getTotalGains().add(capitalGain));
//            }
//
//            // Add the proceeds from the sale to the cash balance.
//            context.setCashBalance(context.getCashBalance().add(sellAmount));
//
//            // Decrease the remaining withdrawal needed by the sell amount.
//            withdrawalNeeded = withdrawalNeeded.subtract(sellAmount);
//        }
//    }

    // Get list of investments in the expense withdrawal strategy order.
    // 실제 구현에서는 시나리오의 expense withdrawal 전략(예: 투자 ID 목록 등)을 기반으로 투자 목록을 조회해야 합니다.
//    private List<Investment> getWithdrawalStrategyInvestments(Scenario scenario) {
//        // TODO: Implement the retrieval of investments based on the expense withdrawal strategy defined in the scenario.
//        return investmentRepository.findInvestmentsForWithdrawalByScenarioId(scenario.getId());
//    }

}
