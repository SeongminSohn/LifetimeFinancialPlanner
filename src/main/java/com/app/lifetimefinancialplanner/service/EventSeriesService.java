package com.app.lifetimefinancialplanner.service;

import com.app.lifetimefinancialplanner.domain.entity.EventSeries;
import com.app.lifetimefinancialplanner.repository.EventSeriesRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventSeriesService {

    private final EventSeriesRepository eventSeriesRepository;

    public EventSeriesService(EventSeriesRepository eventSeriesRepository) {
        this.eventSeriesRepository = eventSeriesRepository;
    }

    public List<EventSeries> getEventSeriesByScenarioAndType(Long scenarioId, String eventType) {
        return eventSeriesRepository.findAllByScenarioIdAndEventType(scenarioId, eventType);
    }
}
