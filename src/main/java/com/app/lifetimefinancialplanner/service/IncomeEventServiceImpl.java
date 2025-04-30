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

        EventSeries eventSeries = EventSeries.builder()
                .scenario(scenario)
                .name(incomeEventDTO.getName())
                .startYear(distributionService.convertDTOToEmbeddable(incomeEventDTO.getStartYear()))
                .duration(distributionService.convertDTOToEmbeddable(incomeEventDTO.getDuration()))
                .eventType(incomeEventDTO.getEventType())
                .build();
        eventSeries = eventSeriesRepository.save(eventSeries);

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
        IncomeEvent existing = incomeEventRepository.findById(eventSeriesId)
                .orElseThrow(() -> new IllegalArgumentException("IncomeEvent not found with id: " + eventSeriesId));

        EventSeries es = existing.getEventSeries();
        EventSeries updatedSeries = es.toBuilder()
                .name(incomeEventDTO.getName() != null ? incomeEventDTO.getName() : es.getName())
                .startYear(incomeEventDTO.getStartYear() != null
                        ? distributionService.convertDTOToEmbeddable(incomeEventDTO.getStartYear())
                        : es.getStartYear())
                .duration(incomeEventDTO.getDuration() != null
                        ? distributionService.convertDTOToEmbeddable(incomeEventDTO.getDuration())
                        : es.getDuration())
                .eventType(incomeEventDTO.getEventType() != null ? incomeEventDTO.getEventType() : es.getEventType())
                .build();
        updatedSeries = eventSeriesRepository.save(updatedSeries);

        IncomeEvent updated = existing.toBuilder()
                .initialAmount(incomeEventDTO.getInitialAmount() != null ? incomeEventDTO.getInitialAmount() : existing.getInitialAmount())
                .annualChange(incomeEventDTO.getAnnualChange() != null
                        ? distributionService.convertDTOToEmbeddable(incomeEventDTO.getAnnualChange())
                        : existing.getAnnualChange())
                .inflationAdjustment(incomeEventDTO.getInflationAdjustment() != null ? incomeEventDTO.getInflationAdjustment() : existing.getInflationAdjustment())
                .userPercentage(incomeEventDTO.getUserPercentage() != null ? incomeEventDTO.getUserPercentage() : existing.getUserPercentage())
                .isSocialSecurity(incomeEventDTO.getIsSocialSecurity() != null ? incomeEventDTO.getIsSocialSecurity() : existing.getIsSocialSecurity())
                .build();

        return incomeEventRepository.save(updated);
    }

    @Override
    @Transactional
    public void deleteIncomeEvent(Long eventSeriesId) {
        IncomeEvent existing = incomeEventRepository.findById(eventSeriesId)
                .orElseThrow(() -> new IllegalArgumentException("IncomeEvent not found with id: " + eventSeriesId));
        eventSeriesRepository.delete(existing.getEventSeries());
        incomeEventRepository.delete(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public List<IncomeEventDTO> getIncomeEventListByScenarioId(Long scenarioId) {
        return incomeEventRepository.findAll().stream()
                .filter(ie -> {
                    EventSeries es = ie.getEventSeries();
                    return es != null
                            && es.getScenario() != null
                            && scenarioId.equals(es.getScenario().getId())
                            && "INCOME".equalsIgnoreCase(es.getEventType());
                })
                .map(ie -> {
                    IncomeEventDTO dto = new IncomeEventDTO();
                    dto.setEventSeriesId(ie.getEventSeriesId());
                    dto.setScenarioId(ie.getEventSeries().getScenario().getId());
                    dto.setName(ie.getEventSeries().getName());
                    dto.setStartYear(distributionService.convertEmbeddableToDTO(ie.getEventSeries().getStartYear()));
                    dto.setDuration(distributionService.convertEmbeddableToDTO(ie.getEventSeries().getDuration()));
                    dto.setEventType(ie.getEventSeries().getEventType());
                    dto.setInitialAmount(ie.getInitialAmount());
                    dto.setAnnualChange(distributionService.convertEmbeddableToDTO(ie.getAnnualChange()));
                    dto.setInflationAdjustment(ie.getInflationAdjustment());
                    dto.setUserPercentage(ie.getUserPercentage());
                    dto.setIsSocialSecurity(ie.getIsSocialSecurity());
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

        if (context.getUpdatedIncomeEvents() == null || context.getUpdatedIncomeEvents().isEmpty()) {
            log.info("▶ runIncomeEvents: loading from DB for Scenario ID={}", scenario.getId());
            incomeEventList = incomeEventRepository.findAllByEventSeries_Scenario_Id(scenario.getId());
            if (incomeEventList == null || incomeEventList.isEmpty()) {
                log.error("No IncomeEvent data found in DB for Scenario ID: {}", scenario.getId());
                throw new IllegalArgumentException("There is no IncomeEvent Information for Scenario ID: " + scenario.getId());
            }
        } else {
            log.info("▶ runIncomeEvents: using updated events from context for Scenario ID={}", scenario.getId());
            incomeEventList = context.getUpdatedIncomeEvents();
        }

        for (IncomeEvent ie : incomeEventList) {
            EventSeries es = ie.getEventSeries();
            int startYear = (int) samplingService.sample(distributionService.convertEmbeddableToDTO(es.getStartYear()));
            int duration = (int) samplingService.sample(distributionService.convertEmbeddableToDTO(es.getDuration()));
            int endYear = startYear + duration;

            log.info("IncomeEvent ID={} start={}, duration={}, end={}, simulationYear={}",
                    ie.getEventSeriesId(), startYear, duration, endYear, currentYear);

            if (currentYear >= startYear && currentYear < endYear) {
                BigDecimal change = BigDecimal.valueOf(samplingService.sample(distributionService.convertEmbeddableToDTO(ie.getAnnualChange())));
                BigDecimal amount = BigDecimal.valueOf(ie.getInitialAmount()).add(change);
                if ("Y".equalsIgnoreCase(ie.getInflationAdjustment())) {
                    amount = amount.multiply(BigDecimal.valueOf(context.getInflationFactor()));
                }
                double pct = getEffectivePercentage(userAlive, spouseAlive, ie);
                amount = amount.multiply(BigDecimal.valueOf(pct));

                if ("Y".equalsIgnoreCase(ie.getIsSocialSecurity())) {
                    context.setCurYearSS(context.getCurYearSS().add(amount));
                } else {
                    context.setCurYearIncome(context.getCurYearIncome().add(amount));
                }

                updatedIncomeEventList.add(ie.toBuilder().initialAmount(amount.doubleValue()).build());
                log.info("Processed IncomeEvent ID={} amount={}", ie.getEventSeriesId(), amount);
            } else {
                log.info("Skipped IncomeEvent ID={} for simulationYear={}", ie.getEventSeriesId(), currentYear);
            }
        }

        context.setUpdatedIncomeEvents(updatedIncomeEventList);
        log.info("runIncomeEvents completed: updatedEvents size={} for scenarioId={}", updatedIncomeEventList.size(), scenario.getId());
    }

    private static double getEffectivePercentage(Boolean userAlive, Boolean spouseAlive, IncomeEvent ie) {
        double up = ie.getUserPercentage() != null ? ie.getUserPercentage() : 0.0;
        if (userAlive && spouseAlive) return 1.0;
        if (userAlive) return up;
        if (spouseAlive) return 1 - up;
        return 0.0;
    }
}
