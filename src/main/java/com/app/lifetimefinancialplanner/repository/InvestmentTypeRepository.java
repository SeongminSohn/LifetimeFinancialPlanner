package com.app.lifetimefinancialplanner.repository;

import com.app.lifetimefinancialplanner.domain.entity.InvestmentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvestmentTypeRepository extends JpaRepository<InvestmentType, Long> {
    List<InvestmentType> findAllByScenarioId(Long scenarioId);
}
