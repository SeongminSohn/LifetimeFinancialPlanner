package com.app.lifetimefinancialplanner.repository;

import com.app.lifetimefinancialplanner.domain.entity.InvestEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvestEventRepository extends JpaRepository<InvestEvent, Long> {
    List <InvestEvent> findAllByEventSeries_Scenario_Id(Long scenarioId);

}
