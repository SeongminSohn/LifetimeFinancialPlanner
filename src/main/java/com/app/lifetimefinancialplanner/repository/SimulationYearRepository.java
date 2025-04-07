package com.app.lifetimefinancialplanner.repository;

import com.app.lifetimefinancialplanner.domain.entity.SimulationYear;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SimulationYearRepository extends JpaRepository<SimulationYear, Long> {
    List<SimulationYear> findBySimulationId(Long simulationId);
}
