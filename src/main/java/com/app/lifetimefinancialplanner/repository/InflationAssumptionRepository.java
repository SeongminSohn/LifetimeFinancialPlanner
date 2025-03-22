package com.app.lifetimefinancialplanner.repository;

import com.app.lifetimefinancialplanner.domain.entity.InflationAssumption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InflationAssumptionRepository extends JpaRepository<InflationAssumption, Long> {
}

