package com.app.lifetimefinancialplanner.service;

import com.app.lifetimefinancialplanner.domain.context.SimulationContext;
import com.app.lifetimefinancialplanner.domain.dto.ExpenseWithdrawalStrategyDTO;
import com.app.lifetimefinancialplanner.domain.entity.ExpenseWithdrawalStrategy;
import com.app.lifetimefinancialplanner.domain.entity.Investment;
import com.app.lifetimefinancialplanner.domain.entity.Scenario;
import com.app.lifetimefinancialplanner.repository.ExpenseWithdrawalStrategyRepository;
import com.app.lifetimefinancialplanner.repository.InvestmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExpenseWithdrawalStrategyServiceImpl implements ExpenseWithdrawalStrategyService {

    private static final Logger log = LoggerFactory.getLogger(InvestEventServiceImpl.class);
    private final ExpenseWithdrawalStrategyRepository strategyRepository;
    private final InvestmentRepository investmentRepository;

    public ExpenseWithdrawalStrategyServiceImpl(ExpenseWithdrawalStrategyRepository strategyRepository,
                                                InvestmentRepository investmentRepository) {
        this.strategyRepository = strategyRepository;
        this.investmentRepository = investmentRepository;
    }

    @Override
    @Transactional
    public ExpenseWithdrawalStrategy createExpenseWithdrawalStrategy(ExpenseWithdrawalStrategyDTO dto) {
        ExpenseWithdrawalStrategy strategy = ExpenseWithdrawalStrategy.builder()
                .scenarioId(dto.getScenarioId())
                .sellingOrder(dto.getSellingOrder())
                .build();
        return strategyRepository.save(strategy);
    }

    @Override
    public ExpenseWithdrawalStrategy getExpenseWithdrawalStrategy(Long id) {
        return strategyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ExpenseWithdrawalStrategy not found with id: " + id));
    }

    @Override
    @Transactional
    public ExpenseWithdrawalStrategy updateExpenseWithdrawalStrategy(Long id, ExpenseWithdrawalStrategyDTO dto) {
        ExpenseWithdrawalStrategy existing = strategyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ExpenseWithdrawalStrategy not found with id: " + id));
        ExpenseWithdrawalStrategy updated = existing.toBuilder()
                .sellingOrder(dto.getSellingOrder() != null ? dto.getSellingOrder() : existing.getSellingOrder())
                .build();
        return strategyRepository.save(updated);
    }

    @Override
    @Transactional
    public void deleteExpenseWithdrawalStrategy(Long id) {
        ExpenseWithdrawalStrategy existing = getExpenseWithdrawalStrategy(id);
        strategyRepository.delete(existing);
    }

    @Override
    public ExpenseWithdrawalStrategy getExpenseWithdrawalStrategyByScenarioId(Long scenarioId) {
        return strategyRepository.findByScenarioId(scenarioId);
    }

    @Override
    @Transactional
    public void withdrawFundsForExpenses(Scenario scenario, SimulationContext context, BigDecimal withdrawalNeeded) {
        int currentYear = context.getCurrentYear();
        List<Investment> withdrawalInvestments = context.getUpdatedInvestments();

        // If currentYear is the actual current year, fetch IncomeEvents from DB
        if (context.getInvestmentsPurchasingPrices() == null || context.getInvestmentsPurchasingPrices().isEmpty()) {
            // If no purchase price records yet, initialize them using current investments.
            if (withdrawalInvestments == null || withdrawalInvestments.isEmpty()) {
                withdrawalInvestments = investmentRepository.findAllByScenarioId(scenario.getId());
                if (withdrawalInvestments == null || withdrawalInvestments.isEmpty()) {
                    throw new IllegalArgumentException("There is no Investment Information for Scenario ID: " + scenario.getId());
                }
            }
            List<Investment> purchasePriceRecords = new ArrayList<>();
            for (Investment investment : withdrawalInvestments) {
                Investment record = investment.toBuilder()
                        .value(investment.getValue())
                        .build();
                purchasePriceRecords.add(record);
            }
            context.setInvestmentsPurchasingPrices(purchasePriceRecords);
        }

        // Determine which investments to withdraw
        if (withdrawalInvestments == null || withdrawalInvestments.isEmpty()) {
            withdrawalInvestments = context.getUpdatedInvestments();
        }
        if (withdrawalInvestments == null || withdrawalInvestments.isEmpty()) {
            throw new IllegalArgumentException("There is no Investment Information for Scenario ID: " + scenario.getId());
        }

        // Retrieve the purchase price records from context
        List<Investment> purchasePriceRecords = context.getInvestmentsPurchasingPrices();
        if (purchasePriceRecords == null) {
            throw new IllegalArgumentException("Purchase price records are not initialized for Scenario ID: " + scenario.getId());
        }

        // Citation: GPT helped me how to sort the investments
        // Sort investments in expense withdrawal strategy's selling order.
        ExpenseWithdrawalStrategy strategy = getExpenseWithdrawalStrategyByScenarioId(scenario.getId());
        if (strategy != null && strategy.getSellingOrder() != null && !strategy.getSellingOrder().isEmpty()) {
            List<String> sellingOrder = strategy.getSellingOrder();
            withdrawalInvestments.sort((inv1, inv2) -> {
                String id1 = inv1.getId().toString();
                String id2 = inv2.getId().toString();
                int index1 = sellingOrder.indexOf(id1);
                int index2 = sellingOrder.indexOf(id2);
                if (index1 < 0) index1 = Integer.MAX_VALUE;
                if (index2 < 0) index2 = Integer.MAX_VALUE;
                return Integer.compare(index1, index2);
            });
        }

        List<Investment> updatedInvestmentsList = new ArrayList<>();
        BigDecimal totalWithdrawn = BigDecimal.ZERO;

        // Process withdrawals by iterating sorted list of investments
        for (Investment investment : withdrawalInvestments) {
            if (withdrawalNeeded.compareTo(BigDecimal.ZERO) <= 0) {
                updatedInvestmentsList.add(investment);
                continue;
            }

            BigDecimal investmentValue = BigDecimal.valueOf(investment.getValue());
            if (investmentValue.compareTo(BigDecimal.ZERO) <= 0) {
                updatedInvestmentsList.add(investment);
                continue;
            }

            // Determine sell amount for capitalGains.
            BigDecimal sellAmount = (investmentValue.compareTo(withdrawalNeeded) >= 0) ? withdrawalNeeded : investmentValue;
            totalWithdrawn = totalWithdrawn.add(sellAmount);
            log.info("Withdrew {} for expenses from Investment ID: {}", sellAmount, investment.getId()); // new log


            BigDecimal preSaleValue = investmentValue;
            BigDecimal afterSaleValue = investmentValue.subtract(sellAmount);

            // Retrieve corresponding purchase price record from investmentsPurchasingPrices.
            Investment purchaseRecord = purchasePriceRecords.stream()
                    .filter(rec -> rec.getId().equals(investment.getId()))
                    .findFirst()
                    .orElse(null);

            // If no record exists, use initial investment value as purchase price.
            BigDecimal purchasePrice = (purchaseRecord != null)
                    ? BigDecimal.valueOf(purchaseRecord.getValue())
                    : BigDecimal.valueOf(investment.getValue());

            // Compute sell fraction (f = sellAmount / preSaleValue)
            BigDecimal fraction = sellAmount.divide(preSaleValue, 10, RoundingMode.HALF_UP);

            // Compute capital gain (f * (preSaleValue - purchasePrice))
            BigDecimal capitalGain = fraction.multiply(preSaleValue.subtract(purchasePrice));

            if ("NON-RETIREMENT".equalsIgnoreCase(investment.getTaxStatus())) {
                context.setCurYearGains(context.getCurYearGains().add(capitalGain));
            }

            // Update cash balance.
            context.setCashBalance(context.getCashBalance().add(sellAmount));
            withdrawalNeeded = withdrawalNeeded.subtract(sellAmount);

            // Update purchase price for the sold investment (newPurchasePrice = (1 - f) * purchasePrice)
            BigDecimal newPurchasePrice = BigDecimal.ONE.subtract(fraction).multiply(purchasePrice);

            // Update the purchase record in purchasePriceRecords.
            if (purchaseRecord != null) {
                Investment updatedPurchaseRecord = purchaseRecord.toBuilder()
                        .value(newPurchasePrice.doubleValue())
                        .build();

                // Replace old record in the list.
                purchasePriceRecords.remove(purchaseRecord);
                purchasePriceRecords.add(updatedPurchaseRecord);
            }
            else {
                // If no record existed, create one using the newPurchasePrice.
                Investment newRecord = investment.toBuilder()
                        .value(newPurchasePrice.doubleValue())
                        .build();
                purchasePriceRecords.add(newRecord);
            }

            // Update the investment's market value after sale.
            Investment updatedInvestment = investment.toBuilder()
                    .value(afterSaleValue.doubleValue())
                    .build();
            updatedInvestmentsList.add(updatedInvestment);
        }

        // Update simulation context
        context.setTotalExpenses(totalWithdrawn);
        context.setUpdatedInvestments(updatedInvestmentsList);
        context.setInvestmentsPurchasingPrices(purchasePriceRecords);
        log.info("Total expenses withdrawn this year: {}", totalWithdrawn);
    }

}
