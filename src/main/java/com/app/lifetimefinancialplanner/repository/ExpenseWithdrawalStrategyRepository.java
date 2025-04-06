package com.app.lifetimefinancialplanner.repository;

import com.app.lifetimefinancialplanner.domain.entity.ExpenseWithdrawalStrategy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseWithdrawalStrategyRepository extends JpaRepository<ExpenseWithdrawalStrategy, Long> {
    List<ExpenseWithdrawalStrategy> findByScenarioId(Long scenarioId);
}
