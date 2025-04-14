package com.app.lifetimefinancialplanner.service;

import com.app.lifetimefinancialplanner.domain.dto.ExpenseEventDTO;
import com.app.lifetimefinancialplanner.domain.embeddable.DistributionEmbeddable;
import com.app.lifetimefinancialplanner.domain.entity.EventSeries;
import com.app.lifetimefinancialplanner.domain.entity.ExpenseEvent;
import com.app.lifetimefinancialplanner.domain.entity.Scenario;
import com.app.lifetimefinancialplanner.repository.EventSeriesRepository;
import com.app.lifetimefinancialplanner.repository.ExpenseEventRepository;
import com.app.lifetimefinancialplanner.repository.ScenarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpenseEventServiceImpl implements ExpenseEventService {

    private final ExpenseEventRepository expenseEventRepository;
    private final EventSeriesRepository eventSeriesRepository;
    private final ScenarioRepository scenarioRepository;
    private final DistributionService distributionService;
    private final SamplingService samplingService;

    public ExpenseEventServiceImpl(ExpenseEventRepository expenseEventRepository,
                                   EventSeriesRepository eventSeriesRepository,
                                   ScenarioRepository scenarioRepository,
                                   DistributionService distributionService,
                                   SamplingService samplingService) {
        this.expenseEventRepository = expenseEventRepository;
        this.eventSeriesRepository = eventSeriesRepository;
        this.scenarioRepository = scenarioRepository;
        this.distributionService = distributionService;
        this.samplingService = samplingService;
    }

    @Override
    @Transactional
    public ExpenseEvent createExpenseEvent(ExpenseEventDTO expenseEventDTO) {
        Scenario scenario = scenarioRepository.findById(expenseEventDTO.getScenarioId())
                .orElseThrow(() -> new IllegalArgumentException("Scenario not found with id: " + expenseEventDTO.getScenarioId()));

        EventSeries eventSeries = EventSeries.builder()
                .scenario(scenario)
                .name(expenseEventDTO.getName())
                .startYear(distributionService.convertDTOToEmbeddable(expenseEventDTO.getStartYear()))
                .duration(distributionService.convertDTOToEmbeddable(expenseEventDTO.getDuration()))
                .eventType(expenseEventDTO.getEventType())
                .build();

        eventSeries = eventSeriesRepository.save(eventSeries);

        // Convert DistributionDTO to DistributionEmbeddable for annualChange
        DistributionEmbeddable annualChangeEmb = distributionService.convertDTOToEmbeddable(expenseEventDTO.getAnnualChange());

        // Create ExpenseEvent entity
        ExpenseEvent expenseEvent = ExpenseEvent.builder()
                .initialAmount(expenseEventDTO.getInitialAmount())
                .annualChange(annualChangeEmb)
                .inflationAdjustment(expenseEventDTO.getInflationAdjustment())
                .userPercentage(expenseEventDTO.getUserPercentage())
                .isDiscretionary(expenseEventDTO.getIsDiscretionary())
                .eventSeries(eventSeries)
                .build();

        return expenseEventRepository.save(expenseEvent);
    }

    @Override
    public ExpenseEvent getExpenseEvent(Long eventSeriesId) {
        return expenseEventRepository.findById(eventSeriesId)
                .orElseThrow(() -> new IllegalArgumentException("ExpenseEvent not found with id: " + eventSeriesId));
    }

    @Override
    @Transactional
    public ExpenseEvent updateExpenseEvent(Long eventSeriesId, ExpenseEventDTO expenseEventDTO) {
        // Retrieve existing ExpenseEvent
        ExpenseEvent existingExpenseEvent = expenseEventRepository.findById(eventSeriesId)
                .orElseThrow(() -> new IllegalArgumentException("ExpenseEvent not found with id: " + eventSeriesId));

        // Update associated EventSeries (if any fields are provided)
        EventSeries existingEventSeries = existingExpenseEvent.getEventSeries();
        EventSeries updatedEventSeries = existingEventSeries.toBuilder()
                .name(expenseEventDTO.getName() != null ? expenseEventDTO.getName() : existingEventSeries.getName())
                .startYear(expenseEventDTO.getStartYear() != null
                        ? distributionService.convertDTOToEmbeddable(expenseEventDTO.getStartYear())
                        : existingEventSeries.getStartYear())
                .duration(expenseEventDTO.getDuration() != null
                        ? distributionService.convertDTOToEmbeddable(expenseEventDTO.getDuration())
                        : existingEventSeries.getDuration())
                .eventType(expenseEventDTO.getEventType() != null ? expenseEventDTO.getEventType() : existingEventSeries.getEventType())
                .build();
        updatedEventSeries = eventSeriesRepository.save(updatedEventSeries);

        // Update ExpenseEvent fields
        ExpenseEvent updatedExpenseEvent = existingExpenseEvent.toBuilder()
                .initialAmount(expenseEventDTO.getInitialAmount() != null ? expenseEventDTO.getInitialAmount() : existingExpenseEvent.getInitialAmount())
                .annualChange(expenseEventDTO.getAnnualChange() != null
                        ? distributionService.convertDTOToEmbeddable(expenseEventDTO.getAnnualChange())
                        : existingExpenseEvent.getAnnualChange())
                .inflationAdjustment(expenseEventDTO.getInflationAdjustment() != null ? expenseEventDTO.getInflationAdjustment() : existingExpenseEvent.getInflationAdjustment())
                .userPercentage(expenseEventDTO.getUserPercentage() != null ? expenseEventDTO.getUserPercentage() : existingExpenseEvent.getUserPercentage())
                .isDiscretionary(expenseEventDTO.getIsDiscretionary() != null ? expenseEventDTO.getIsDiscretionary() : existingExpenseEvent.getIsDiscretionary())
                .build();

        return expenseEventRepository.save(updatedExpenseEvent);
    }

    @Override
    @Transactional
    public void deleteExpenseEvent(Long eventSeriesId) {
        ExpenseEvent existingExpenseEvent = expenseEventRepository.findById(eventSeriesId)
                .orElseThrow(() -> new IllegalArgumentException("ExpenseEvent not found with id: " + eventSeriesId));

        // Optionally delete the associated EventSeries if not needed anymore
        eventSeriesRepository.delete(existingExpenseEvent.getEventSeries());
        expenseEventRepository.delete(existingExpenseEvent);
    }

    @Override
    public List<ExpenseEventDTO> getExpenseEventsBySeriesId(Long scenarioId) {
        return expenseEventRepository.findAll().stream()
                .filter(expenseEvent -> {
                    EventSeries es = expenseEvent.getEventSeries();
                    if (es == null || es.getScenario() == null) {
                        return false;
                    }
                    boolean matchScenario = scenarioId.equals(es.getScenario().getId());
                    boolean matchEventType = "EXPENSE".equalsIgnoreCase(es.getEventType());
                    return matchScenario && matchEventType;
                })
                .map(expenseEvent -> {
                    ExpenseEventDTO dto = new ExpenseEventDTO();
                    dto.setEventSeriesId(expenseEvent.getEventSeriesId());
                    dto.setScenarioId(expenseEvent.getEventSeries().getScenario().getId());
                    dto.setName(expenseEvent.getEventSeries().getName());
                    dto.setStartYear(distributionService.convertEmbeddableToDTO(expenseEvent.getEventSeries().getStartYear()));
                    dto.setDuration(distributionService.convertEmbeddableToDTO(expenseEvent.getEventSeries().getDuration()));
                    dto.setEventType(expenseEvent.getEventSeries().getEventType());
                    dto.setInitialAmount(expenseEvent.getInitialAmount());
                    dto.setAnnualChange(distributionService.convertEmbeddableToDTO(expenseEvent.getAnnualChange()));
                    dto.setInflationAdjustment(expenseEvent.getInflationAdjustment());
                    dto.setUserPercentage(expenseEvent.getUserPercentage());
                    dto.setIsDiscretionary(expenseEvent.getIsDiscretionary());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BigDecimal calculateNonDiscretionaryExpense(Scenario scenario, int simulationYear, double inflationFactor) {
        // Retrieve all ExpenseEvents for the scenario (e.g., via expenseEventRepository.findAllByScenarioId(scenario.getId()))
        List<ExpenseEvent> expenseEvents = expenseEventRepository.findAllByEventSeries_Scenario_Id(scenario.getId());
        BigDecimal totalExpense = BigDecimal.ZERO;
        for (ExpenseEvent event : expenseEvents) {
            // Get event series parameters by sampling
            int eventStartYear = (int) samplingService.sample(distributionService.convertEmbeddableToDTO(event.getEventSeries().getStartYear()));
            int eventDuration = (int) samplingService.sample(distributionService.convertEmbeddableToDTO(event.getEventSeries().getDuration()));
            int eventEndYear = eventStartYear + eventDuration;

            // Process only if simulationYear falls in the event period
            if (simulationYear >= eventStartYear && simulationYear < eventEndYear) {
                BigDecimal annualChange = BigDecimal.valueOf(samplingService.sample(distributionService.convertEmbeddableToDTO(event.getAnnualChange())));
                BigDecimal baseAmount = BigDecimal.valueOf(event.getInitialAmount());
                BigDecimal eventExpense = baseAmount.add(annualChange);
                if ("Y".equalsIgnoreCase(event.getInflationAdjustment())) {
                    eventExpense = eventExpense.multiply(BigDecimal.valueOf(inflationFactor));
                }
                eventExpense = eventExpense.multiply(BigDecimal.valueOf(event.getUserPercentage()));
                totalExpense = totalExpense.add(eventExpense);
            }
        }
        return totalExpense;
    }

}
