package com.app.lifetimefinancialplanner.repository;

import com.app.lifetimefinancialplanner.domain.entity.Scenario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScenarioRepository extends JpaRepository<Scenario, Long> {
}
