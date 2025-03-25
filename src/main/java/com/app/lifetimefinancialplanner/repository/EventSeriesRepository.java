package com.app.lifetimefinancialplanner.repository;

import com.app.lifetimefinancialplanner.domain.entity.EventSeries;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventSeriesRepository extends JpaRepository<EventSeries, Long> {
    // Find all EventSeries for a given scenario ID AND eventType
    List<EventSeries> findAllByScenarioIdAndEventType(Long scenarioId, String eventType);
}
