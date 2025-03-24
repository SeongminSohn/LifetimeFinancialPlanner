package com.app.lifetimefinancialplanner.repository;

import com.app.lifetimefinancialplanner.domain.entity.ExpenseEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseEventRepository extends JpaRepository<ExpenseEvent, Long> {
}
