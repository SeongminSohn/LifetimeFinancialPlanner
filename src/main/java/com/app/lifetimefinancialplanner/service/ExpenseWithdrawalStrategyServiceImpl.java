package com.app.lifetimefinancialplanner.service;

import com.app.lifetimefinancialplanner.domain.context.SimulationContext;
import com.app.lifetimefinancialplanner.domain.dto.ExpenseWithdrawalStrategyDTO;
import com.app.lifetimefinancialplanner.domain.entity.ExpenseWithdrawalStrategy;
import com.app.lifetimefinancialplanner.domain.entity.Investment;
import com.app.lifetimefinancialplanner.domain.entity.Scenario;
import com.app.lifetimefinancialplanner.repository.ExpenseWithdrawalStrategyRepository;
import com.app.lifetimefinancialplanner.repository.InvestmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExpenseWithdrawalStrategyServiceImpl implements ExpenseWithdrawalStrategyService {

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
        List<Investment> withdrawalInvestments;

        // For the first simulation year, load investments from the database; for later years, use updated investments from context.
        if (currentYear == LocalDateTime.now().getYear()) {
            withdrawalInvestments = investmentRepository.findAllByScenarioId(scenario.getId());
            if (withdrawalInvestments == null || withdrawalInvestments.isEmpty()) {
                throw new IllegalArgumentException("There is no Investment Information for Scenario ID: " + scenario.getId());
            }
        } else {
            withdrawalInvestments = context.getUpdatedInvestments();
            if (withdrawalInvestments == null || withdrawalInvestments.isEmpty()) {
                throw new IllegalArgumentException("There is no updated Investment Information for Scenario ID: " + scenario.getId());
            }
        }

        // Sort investments according to the expense withdrawal strategy's selling order.
        // Retrieve the expense withdrawal strategy for the scenario.
        ExpenseWithdrawalStrategy strategy = getExpenseWithdrawalStrategyByScenarioId(scenario.getId());
        if (strategy != null && strategy.getSellingOrder() != null && !strategy.getSellingOrder().isEmpty()) {
            List<String> sellingOrder = strategy.getSellingOrder();

            // Citation: GPT helped me to how to sort the investments
            // Sort the investments by the index of their IDs (converted to String) in the sellingOrder list.
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

        // Initialize a new list to collect updated investments
        List<Investment> updatedInvestmentsList = new ArrayList<>();

        // Iterate over each investment in the sorted list to process withdrawals.
        for (Investment investment : withdrawalInvestments) {
            // If the required withdrawal amount has been met, add the remaining investments as-is.
            if (withdrawalNeeded.compareTo(BigDecimal.ZERO) <= 0) {
                updatedInvestmentsList.add(investment);
                continue;
            }

            BigDecimal investmentValue = BigDecimal.valueOf(investment.getValue());
            // Skip if the investment's value is zero or negative.
            if (investmentValue.compareTo(BigDecimal.ZERO) <= 0) {
                updatedInvestmentsList.add(investment);
                continue;
            }

            // Determine the amount to sell: the lesser of the current investment value and the remaining withdrawal needed.
            BigDecimal sellAmount = investmentValue.compareTo(withdrawalNeeded) >= 0 ? withdrawalNeeded : investmentValue;
            BigDecimal preSaleValue = investmentValue;
            BigDecimal afterSaleValue = investmentValue.subtract(sellAmount);

            // Compute the proportional capital gain for the sold portion.
//            BigDecimal purchasePrice = investment.getPurchasePrice(); // Assume not null.
//            BigDecimal capitalGain;
//            if (sellAmount.compareTo(preSaleValue) == 0) {
//                capitalGain = preSaleValue.subtract(purchasePrice);
//            } else {
//                BigDecimal fraction = sellAmount.divide(preSaleValue, 10, RoundingMode.HALF_UP);
//                capitalGain = fraction.multiply(preSaleValue.subtract(purchasePrice));
//            }
//            if ("NON-RETIREMENT".equalsIgnoreCase(investment.getTaxStatus())) {
//                context.setCurYearGains(context.getCurYearGains().add(capitalGain));
//            }
//
//            // Increase the cash balance by the sell proceeds.
//            context.setCashBalance(context.getCashBalance().add(sellAmount));
//            // Decrease the remaining withdrawal needed.
//            withdrawalNeeded = withdrawalNeeded.subtract(sellAmount);

            // Create an updated Investment with the reduced value.
            Investment updatedInvestment = investment.toBuilder()
                    .value(afterSaleValue.doubleValue())
                    .build();
            updatedInvestmentsList.add(updatedInvestment);
        }

        // Update the simulation context with the new list of updated investments.
        context.setUpdatedInvestments(updatedInvestmentsList);
    }
}
