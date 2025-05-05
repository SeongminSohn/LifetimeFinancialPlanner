package com.app.lifetimefinancialplanner.service;

import com.app.lifetimefinancialplanner.domain.context.SimulationContext;
import com.app.lifetimefinancialplanner.domain.entity.EventSeries;
import com.app.lifetimefinancialplanner.domain.entity.IncomeEvent;
import com.app.lifetimefinancialplanner.domain.dto.IncomeEventDTO;
import com.app.lifetimefinancialplanner.domain.entity.Scenario;
import com.app.lifetimefinancialplanner.repository.EventSeriesRepository;
import com.app.lifetimefinancialplanner.repository.IncomeEventRepository;
import com.app.lifetimefinancialplanner.repository.ScenarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class IncomeEventServiceImpl implements IncomeEventService {
    private static final Logger log = LoggerFactory.getLogger(IncomeEventServiceImpl.class);

    private final IncomeEventRepository incomeEventRepository;
    private final EventSeriesRepository eventSeriesRepository;
    private final DistributionService distributionService;
    private final ScenarioRepository scenarioRepository;
    private final SamplingService samplingService;

    public IncomeEventServiceImpl(IncomeEventRepository incomeEventRepository,
                                  EventSeriesRepository eventSeriesRepository,
                                  DistributionService distributionService,
                                  ScenarioRepository scenarioRepository,
                                  SamplingService samplingService) {
        this.incomeEventRepository = incomeEventRepository;
        this.eventSeriesRepository = eventSeriesRepository;
        this.distributionService = distributionService;
        this.scenarioRepository = scenarioRepository;
        this.samplingService = samplingService;
    }

    @Override
    @Transactional
    public IncomeEvent createIncomeEvent(IncomeEventDTO incomeEventDTO) {
        Scenario scenario = scenarioRepository.findById(incomeEventDTO.getScenarioId())
                .orElseThrow(() -> new IllegalArgumentException("Scenario not found with id: " + incomeEventDTO.getScenarioId()));

        // Create EventSeries first in order to include it in IncomeEvent
        EventSeries eventSeries = EventSeries.builder()
                .scenario(scenario)
                .name(incomeEventDTO.getName())
                .startYear(distributionService.convertDTOToEmbeddable(incomeEventDTO.getStartYear()))
                .duration(distributionService.convertDTOToEmbeddable(incomeEventDTO.getDuration()))
                .eventType(incomeEventDTO.getEventType())
                .build();

        eventSeries = eventSeriesRepository.save(eventSeries);

        // Create IncomeEvent entity
        IncomeEvent incomeEvent = IncomeEvent.builder()
                .initialAmount(incomeEventDTO.getInitialAmount())
                .annualChange(distributionService.convertDTOToEmbeddable(incomeEventDTO.getAnnualChange()))
                .inflationAdjustment(incomeEventDTO.getInflationAdjustment())
                .userPercentage(incomeEventDTO.getUserPercentage())
                .isSocialSecurity(incomeEventDTO.getIsSocialSecurity())
                .eventSeries(eventSeries)
                .build();

        return incomeEventRepository.save(incomeEvent);
    }

    @Override
    public IncomeEvent getIncomeEvent(Long eventSeriesId) {
        return incomeEventRepository.findById(eventSeriesId)
                .orElseThrow(() -> new IllegalArgumentException("IncomeEvent not found with id: " + eventSeriesId));
    }

    @Override
    @Transactional
    public IncomeEvent updateIncomeEvent(Long eventSeriesId, IncomeEventDTO incomeEventDTO) {
        // Retrieve the existing IncomeEvent
        IncomeEvent existingIncomeEvent = incomeEventRepository.findById(eventSeriesId)
                .orElseThrow(() -> new IllegalArgumentException("IncomeEvent not found with id: " + eventSeriesId));

        // Update associated EventSeries if information is changed
        EventSeries existingEventSeries = existingIncomeEvent.getEventSeries();
        EventSeries updatedEventSeries = existingEventSeries.toBuilder()
                .name(incomeEventDTO.getName() != null
                        ? incomeEventDTO.getName()
                        : existingEventSeries.getName())
                .startYear(incomeEventDTO.getStartYear() != null
                        ? distributionService.convertDTOToEmbeddable(incomeEventDTO.getStartYear())
                        : existingEventSeries.getStartYear())
                .duration(incomeEventDTO.getDuration() != null
                        ? distributionService.convertDTOToEmbeddable(incomeEventDTO.getDuration())
                        : existingEventSeries.getDuration())
                .eventType(incomeEventDTO.getEventType() != null
                        ? incomeEventDTO.getEventType()
                        : existingEventSeries.getEventType())
                .build();
        updatedEventSeries = eventSeriesRepository.save(updatedEventSeries);

        // Update IncomeEvent fields using builder pattern
        IncomeEvent updatedIncomeEvent = existingIncomeEvent.toBuilder()
                .initialAmount(incomeEventDTO.getInitialAmount() != null
                        ? incomeEventDTO.getInitialAmount()
                        : existingIncomeEvent.getInitialAmount())
                .annualChange(incomeEventDTO.getAnnualChange() != null
                        ? distributionService.convertDTOToEmbeddable(incomeEventDTO.getAnnualChange())
                        : existingIncomeEvent.getAnnualChange())
                .inflationAdjustment(incomeEventDTO.getInflationAdjustment() != null
                        ? incomeEventDTO.getInflationAdjustment()
                        : existingIncomeEvent.getInflationAdjustment())
                .userPercentage(incomeEventDTO.getUserPercentage() != null
                        ? incomeEventDTO.getUserPercentage()
                        : existingIncomeEvent.getUserPercentage())
                .isSocialSecurity(incomeEventDTO.getIsSocialSecurity() != null
                        ? incomeEventDTO.getIsSocialSecurity()
                        : existingIncomeEvent.getIsSocialSecurity())
                .eventSeries(updatedEventSeries)
                .build();

        return incomeEventRepository.save(updatedIncomeEvent);
    }

    @Override
    @Transactional
    public void deleteIncomeEvent(Long eventSeriesId) {
        IncomeEvent existingIncomeEvent = incomeEventRepository.findById(eventSeriesId)
                .orElseThrow(() -> new IllegalArgumentException("IncomeEvent not found with id: " + eventSeriesId));

        // Optionally delete the EventSeries as well if cascade is not enabled
        eventSeriesRepository.delete(existingIncomeEvent.getEventSeries());
        incomeEventRepository.delete(existingIncomeEvent);
    }

    @Override
    @Transactional(readOnly = true)
    public List<IncomeEventDTO> getIncomeEventListByScenarioId(Long scenarioId) {

        return incomeEventRepository.findAll().stream()
                // Citation: Got help from ChatGPT on filtering function
                // Filtering IncomeEvent which has scenarioId && eventType("INCOME")
                .filter(incomeEvent -> incomeEvent.getEventSeries() != null
                        && incomeEvent.getEventSeries().getScenario() != null
                        && scenarioId.equals(incomeEvent.getEventSeries().getScenario().getId()))
                // IncomeEvent -> IncomeEventDTO
                .map(incomeEvent -> {
                    IncomeEventDTO dto = new IncomeEventDTO();
                    dto.setEventSeriesId(incomeEvent.getEventSeriesId());
                    dto.setScenarioId(incomeEvent.getEventSeries().getScenario().getId());
                    dto.setName(incomeEvent.getEventSeries().getName());
                    dto.setStartYear(distributionService.convertEmbeddableToDTO(incomeEvent.getEventSeries().getStartYear()));
                    dto.setDuration(distributionService.convertEmbeddableToDTO(incomeEvent.getEventSeries().getDuration()));
                    dto.setEventType(incomeEvent.getEventSeries().getEventType());
                    dto.setInitialAmount(incomeEvent.getInitialAmount());
                    dto.setAnnualChange(distributionService.convertEmbeddableToDTO(incomeEvent.getAnnualChange()));
                    dto.setInflationAdjustment(incomeEvent.getInflationAdjustment());
                    dto.setUserPercentage(incomeEvent.getUserPercentage());
                    dto.setIsSocialSecurity(incomeEvent.getIsSocialSecurity());

                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void runIncomeEvents(Scenario scenario, SimulationContext context, Boolean userAlive, Boolean spouseAlive) {
        int currentYear = context.getCurrentYear();
        List<IncomeEvent> incomeEventList;
        List<IncomeEvent> updatedIncomeEventList = new ArrayList<>();

        // Determine the source of IncomeEvents based on whether the current year equals the actual current year.
        if (currentYear == LocalDateTime.now().getYear()) {
            incomeEventList = incomeEventRepository.findAllByEventSeries_Scenario_Id(scenario.getId());
            if (incomeEventList == null || incomeEventList.isEmpty()) {
                log.error("No IncomeEvent data found in DB for Scenario ID: {}", scenario.getId());
                throw new IllegalArgumentException("There is no IncomeEvent Information and This is Scenario ID: " + scenario.getId());
            }
//            log.info("Current Year branch: Retrieved {} IncomeEvents from DB for Scenario ID: {}", incomeEventList.size(), scenario.getId());
        } else {
            incomeEventList = context.getUpdatedIncomeEvents();
            if (incomeEventList == null) {
                log.error("Updated IncomeEvent data is empty for Scenario ID: {}", scenario.getId());
                throw new IllegalArgumentException("There is no updated IncomeEvent Information and This is Scenario ID: " + scenario.getId());
            }
//            log.info("Non-Current Year branch: Using {} updated IncomeEvents from context for Scenario ID: {}", incomeEventList.size(), scenario.getId());
        }

        // Process each IncomeEvent
        for (IncomeEvent incomeEvent : incomeEventList) {
            EventSeries incomeSeries = incomeEvent.getEventSeries();
            int eventStartYear = (int) samplingService.sample(distributionService.convertEmbeddableToDTO(incomeSeries.getStartYear()));
            int duration = (int) samplingService.sample(distributionService.convertEmbeddableToDTO(incomeSeries.getDuration()));
            int eventEndYear = eventStartYear + duration;
//            log.info("IncomeEvent (ID: {}) - Sampled startYear: {}, duration: {}, computed endYear: {}, current simulation year: {}", incomeEvent.getEventSeriesId(), eventStartYear, duration, eventEndYear, currentYear);

            // Process the IncomeEvent if it is active in the current simulation year.
            if (currentYear >= eventStartYear && currentYear < eventEndYear) {
                BigDecimal annualChange = BigDecimal.valueOf(samplingService.sample(distributionService.convertEmbeddableToDTO(incomeEvent.getAnnualChange())));
                BigDecimal baseAmount = BigDecimal.valueOf(incomeEvent.getInitialAmount());
                BigDecimal currentAmount = baseAmount.add(annualChange);

                if ("Y".equalsIgnoreCase(incomeEvent.getInflationAdjustment())) {
                    currentAmount = currentAmount.multiply(BigDecimal.valueOf(context.getInflationFactor()));
                }

                double effectivePercentage = getEffectivePercentage(userAlive, spouseAlive, incomeEvent);
                currentAmount = currentAmount.multiply(BigDecimal.valueOf(effectivePercentage));

                if ("Y".equalsIgnoreCase(incomeEvent.getIsSocialSecurity())) {
                    context.setCurYearSS(context.getCurYearSS().add(currentAmount));
                } else {
                    context.setCurYearIncome(context.getCurYearIncome().add(currentAmount));
                }

                IncomeEvent updatedIncomeEvent = incomeEvent.toBuilder()
                        .initialAmount(currentAmount.doubleValue())
                        .build();
                updatedIncomeEventList.add(updatedIncomeEvent);
//                log.info("Processed IncomeEvent (ID: {}): Computed currentAmount = {}", incomeEvent.getEventSeriesId(), currentAmount);
            } else {
//                log.info("Skipped IncomeEvent (ID: {}) because the event is not active in the current simulation year: {}", incomeEvent.getEventSeriesId(), currentYear);
            }
        }
        
        context.setUpdatedIncomeEvents(updatedIncomeEventList);
//        log.info("runIncomeEvents completed: updatedIncomeEvents list size = {}", context.getUpdatedIncomeEvents().size());
    }


    private static double getEffectivePercentage(Boolean userAlive, Boolean spouseAlive, IncomeEvent incomeEvent) {
        double userPercent = incomeEvent.getUserPercentage() != null ? incomeEvent.getUserPercentage() : 0.0;
        double effectivePercentage;
        if (userAlive && spouseAlive) {
            effectivePercentage = 1.0;
        } else if (userAlive && !spouseAlive) {
            effectivePercentage = userPercent;
        } else if (!userAlive && spouseAlive) {
            effectivePercentage = 1 - userPercent;
        } else {
            effectivePercentage = 0.0;
        }
        return effectivePercentage;
    }
}

