package com.app.lifetimefinancialplanner.repository;

import com.app.lifetimefinancialplanner.domain.entity.IncomeEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncomeEventRepository extends JpaRepository<IncomeEvent, Long> {
    List<IncomeEvent> findAllByScenarioId(Long scenarioId);
}
