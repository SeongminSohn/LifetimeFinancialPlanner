package com.app.lifetimefinancialplanner.repository;

import com.app.lifetimefinancialplanner.domain.entity.IncomeEvent;
import com.app.lifetimefinancialplanner.domain.entity.Investment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvestmentRepository extends JpaRepository<Investment, Long> {
}
