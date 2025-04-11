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
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IncomeEventServiceImpl implements IncomeEventService {

    private final IncomeEventRepository incomeEventRepository;
    private final EventSeriesRepository eventSeriesRepository;
    private final DistributionService distributionService;
    private final ScenarioRepository scenarioRepository;

    public IncomeEventServiceImpl(IncomeEventRepository incomeEventRepository,
                                  EventSeriesRepository eventSeriesRepository, DistributionService distributionService, ScenarioRepository scenarioRepository) {
        this.incomeEventRepository = incomeEventRepository;
        this.eventSeriesRepository = eventSeriesRepository;
        this.distributionService = distributionService;
        this.scenarioRepository = scenarioRepository;
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
                .name(incomeEventDTO.getName() != null ? incomeEventDTO.getName() : existingEventSeries.getName())
                .startYear(incomeEventDTO.getStartYear() != null
                        ? distributionService.convertDTOToEmbeddable(incomeEventDTO.getStartYear())
                        : existingEventSeries.getStartYear())
                .duration(incomeEventDTO.getDuration() != null
                        ? distributionService.convertDTOToEmbeddable(incomeEventDTO.getDuration())
                        : existingEventSeries.getDuration())
                .eventType(incomeEventDTO.getEventType() != null ? incomeEventDTO.getEventType() : existingEventSeries.getEventType())
                .build();
        updatedEventSeries = eventSeriesRepository.save(updatedEventSeries);

        // Update IncomeEvent fields using builder pattern
        IncomeEvent updatedIncomeEvent = existingIncomeEvent.toBuilder()
                .initialAmount(incomeEventDTO.getInitialAmount() != null ? incomeEventDTO.getInitialAmount() : existingIncomeEvent.getInitialAmount())
                .annualChange(incomeEventDTO.getAnnualChange() != null
                        ? distributionService.convertDTOToEmbeddable(incomeEventDTO.getAnnualChange())
                        : existingIncomeEvent.getAnnualChange())
                .inflationAdjustment(incomeEventDTO.getInflationAdjustment() != null ? incomeEventDTO.getInflationAdjustment() : existingIncomeEvent.getInflationAdjustment())
                .userPercentage(incomeEventDTO.getUserPercentage() != null ? incomeEventDTO.getUserPercentage() : existingIncomeEvent.getUserPercentage())
                .isSocialSecurity(incomeEventDTO.getIsSocialSecurity() != null ? incomeEventDTO.getIsSocialSecurity() : existingIncomeEvent.getIsSocialSecurity())
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
                .filter(incomeEvent -> {
                    EventSeries es = incomeEvent.getEventSeries();
                    if (es == null || es.getScenario() == null) {
                        return false;
                    }
                    boolean matchScenario = scenarioId.equals(es.getScenario().getId());
                    boolean matchEventType = "INCOME".equalsIgnoreCase(es.getEventType());
                    return matchScenario && matchEventType;
                })
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
    public void runIncomeEvents(Scenario scenario, SimulationContext context) {
        // Get list of EventSeries for incomeEvent
        List<EventSeries> incomeEventSeriesList = eventSeriesRepository.findAllByScenarioIdAndEventType(scenario.getId(), "INCOME");
        if (incomeEventSeriesList.isEmpty()) {
            throw new IllegalArgumentException("IncomeEvent not found for scenario id: " + scenario.getId());
        }

        BigDecimal regularIncome = context.getCurYearIncome();
        BigDecimal socialSecurity = context.getCurYearSS();

        for (EventSeries incomeSeries : incomeEventSeriesList) {
            // Get corresponding IncomeEvent with eventSeriesID
            IncomeEvent incomeEvent = incomeEventRepository.findById(incomeSeries.getId())
                    .orElseThrow(() -> new IllegalArgumentException("IncomeEvent not found for EventSeries id: " + incomeSeries.getId()));

            double startYear = incomeSeries.getStartYear().getValue();
            double duration = incomeSeries.getDuration().getValue();
            double endYear = startYear + duration;
            int currentYear = context.getCurrentYear();

            if (currentYear < startYear || currentYear >= endYear){
                double prevAmount = incomeEvent.getInitialAmount();
                double annualChange = incomeEvent.getAnnualChange().getValue();
                double currentAmount = prevAmount + annualChange;

                if ("Y".equalsIgnoreCase(incomeEvent.getInflationAdjustment())) {
                    currentAmount *= context.getInflationFactor();
                }

                // TODO: UserPercentage 관련 개념 confirm 받으면 수정하기
                double userPercent = incomeEvent.getUserPercentage() != null ? incomeEvent.getUserPercentage() : 0.0;
                currentAmount = (1 + userPercent) * currentAmount;

                // Update the value of corresponding income type (regular / social security)
                if ("Y".equalsIgnoreCase(incomeEvent.getIsSocialSecurity())) {
                    context.setCurYearSS(context.getCurYearSS().add(BigDecimal.valueOf(currentAmount)));
                } else {
                    context.setCurYearIncome(context.getCurYearIncome().add(BigDecimal.valueOf(currentAmount)));
                }
            }
        }
    }
}

