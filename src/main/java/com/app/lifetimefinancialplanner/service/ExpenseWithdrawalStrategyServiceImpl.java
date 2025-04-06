package com.app.lifetimefinancialplanner.service;

import com.app.lifetimefinancialplanner.domain.dto.ExpenseWithdrawalStrategyDTO;
import com.app.lifetimefinancialplanner.domain.entity.ExpenseWithdrawalStrategy;
import com.app.lifetimefinancialplanner.repository.ExpenseWithdrawalStrategyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ExpenseWithdrawalStrategyServiceImpl implements ExpenseWithdrawalStrategyService {

    private final ExpenseWithdrawalStrategyRepository strategyRepository;

    public ExpenseWithdrawalStrategyServiceImpl(ExpenseWithdrawalStrategyRepository strategyRepository) {
        this.strategyRepository = strategyRepository;
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

//    @Override
//    public List<ExpenseWithdrawalStrategy> getExpenseWithdrawalStrategiesByScenarioId(Long scenarioId) {
//        return strategyRepository.findByScenarioId(scenarioId);
//    }
}
