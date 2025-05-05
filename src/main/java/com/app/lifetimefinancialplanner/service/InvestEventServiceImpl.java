package com.app.lifetimefinancialplanner.service;

import com.app.lifetimefinancialplanner.domain.context.SimulationContext;
import com.app.lifetimefinancialplanner.domain.dto.InvestEventDTO;
import com.app.lifetimefinancialplanner.domain.embeddable.AllocationEmbeddable;
import com.app.lifetimefinancialplanner.domain.entity.EventSeries;
import com.app.lifetimefinancialplanner.domain.entity.InvestEvent;
import com.app.lifetimefinancialplanner.domain.entity.Investment;
import com.app.lifetimefinancialplanner.domain.entity.Scenario;
import com.app.lifetimefinancialplanner.repository.EventSeriesRepository;
import com.app.lifetimefinancialplanner.repository.InvestEventRepository;
import com.app.lifetimefinancialplanner.repository.InvestmentRepository;
import com.app.lifetimefinancialplanner.repository.ScenarioRepository;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.math.MathContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class InvestEventServiceImpl implements InvestEventService {

    private static final Logger log = LoggerFactory.getLogger(InvestEventServiceImpl.class);
    private final InvestEventRepository investEventRepository;
    private final EventSeriesRepository eventSeriesRepository;
    private final InvestmentRepository investmentRepository;
    private final ScenarioRepository scenarioRepository;
    private final DistributionService distributionService;
    private final AllocationService allocationService;
    private final SamplingService samplingService;

    public InvestEventServiceImpl(InvestEventRepository investEventRepository,
                                  EventSeriesRepository eventSeriesRepository,
                                  InvestmentRepository investmentRepository,
                                  ScenarioRepository scenarioRepository,
                                  DistributionService distributionService,
                                  AllocationService allocationService,
                                  SamplingService samplingService) {
        this.investEventRepository = investEventRepository;
        this.eventSeriesRepository = eventSeriesRepository;
        this.investmentRepository = investmentRepository;
        this.scenarioRepository = scenarioRepository;
        this.distributionService = distributionService;
        this.allocationService = allocationService;
        this.samplingService = samplingService;
    }

    @Override
    @Transactional
    public InvestEvent createInvestEvent(InvestEventDTO dto) {
        // Retrieve Scenario using scenarioId
        Scenario scenario = scenarioRepository.findById(dto.getScenarioId())
                .orElseThrow(() -> new RuntimeException("Scenario not found with id: " + dto.getScenarioId()));

        // Create EventSeries for the InvestEvent using provided fields from DTO.
        EventSeries eventSeries = EventSeries.builder()
                .scenario(scenario)
                .name(dto.getName())
                .startYear(distributionService.convertDTOToEmbeddable(dto.getStartYear()))
                .duration(distributionService.convertDTOToEmbeddable(dto.getDuration()))
                .eventType(dto.getEventType())
                .build();
        eventSeries = eventSeriesRepository.save(eventSeries);

        // Convert assetAllocations DTO list to embeddable list.
        List<AllocationEmbeddable> allocationList =
                allocationService.convertDTOListToEmbeddableList(dto.getAssetAllocations());

        // Build InvestEvent using toBuilder pattern.
        InvestEvent investEvent = InvestEvent.builder()
                .assetAllocations(allocationList)
                .maxCash(dto.getMaxCash())
                .eventSeries(eventSeries)
                .build();

        return investEventRepository.save(investEvent);
    }

    @Override
    public Optional<InvestEvent> getInvestEvent(Long eventSeriesId) {
        return investEventRepository.findById(eventSeriesId);
    }

    @Override
    @Transactional
    public InvestEvent updateInvestEvent(Long eventSeriesId, InvestEventDTO dto) {
        // Retrieve existing InvestEvent
        InvestEvent existingInvestEvent = investEventRepository.findById(eventSeriesId)
                .orElseThrow(() -> new RuntimeException("InvestEvent not found with id: " + eventSeriesId));

        // Update associated EventSeries if information is changed
        EventSeries existingEventSeries = existingInvestEvent.getEventSeries();
        EventSeries updatedEventSeries = existingEventSeries.toBuilder()
                .name(dto.getName() != null
                        ? dto.getName()
                        : existingEventSeries.getName())
                .startYear(dto.getStartYear() != null
                        ? distributionService.convertDTOToEmbeddable(dto.getStartYear())
                        : existingEventSeries.getStartYear())
                .duration(dto.getDuration() != null
                        ? distributionService.convertDTOToEmbeddable(dto.getDuration())
                        : existingEventSeries.getDuration())
                .eventType(dto.getEventType() != null
                        ? dto.getEventType()
                        : existingEventSeries.getEventType())
                .build();
        updatedEventSeries = eventSeriesRepository.save(updatedEventSeries);

        // Determine updated asset allocations
        List<AllocationEmbeddable> updatedAssetAllocations = dto.getAssetAllocations() != null
                ? allocationService.convertDTOListToEmbeddableList(dto.getAssetAllocations())
                : existingInvestEvent.getAssetAllocations();

        // Build and save updated InvestEvent
        InvestEvent updatedInvestEvent = existingInvestEvent.toBuilder()
                .maxCash(dto.getMaxCash() != null
                        ? dto.getMaxCash()
                        : existingInvestEvent.getMaxCash())
                .assetAllocations(updatedAssetAllocations)
                .eventSeries(updatedEventSeries)
                .build();

        return investEventRepository.save(updatedInvestEvent);
    }


    @Override
    @Transactional
    public void deleteInvestEvent(Long eventSeriesId) {
        InvestEvent existing = investEventRepository.findById(eventSeriesId)
                .orElseThrow(() -> new RuntimeException("InvestEvent not found with id: " + eventSeriesId));

        // Optionally delete the EventSeries as well if cascade is not enabled
        eventSeriesRepository.delete(existing.getEventSeries());
        investEventRepository.delete(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvestEventDTO> getInvestEventsByScenarioId(Long scenarioId) {
        return investEventRepository.findAll().stream()
                // Filter events whose associated EventSeries has a Scenario with the given scenarioId.
                .filter(event -> event.getEventSeries() != null
                        && event.getEventSeries().getScenario() != null
                        && scenarioId.equals(event.getEventSeries().getScenario().getId()))
                .map(event -> {
                    InvestEventDTO dto = new InvestEventDTO();
                    dto.setEventSeriesId(event.getEventSeries().getId());
                    dto.setScenarioId(event.getEventSeries().getScenario().getId());
                    dto.setName(event.getEventSeries().getName());
                    dto.setStartYear(distributionService.convertEmbeddableToDTO(event.getEventSeries().getStartYear()));
                    dto.setDuration(distributionService.convertEmbeddableToDTO(event.getEventSeries().getDuration()));
                    dto.setEventType(event.getEventSeries().getEventType());
                    dto.setMaxCash(event.getMaxCash());
                    // Convert the list<AllocationEmbeddable> to List<AllocationDTO>
                    dto.setAssetAllocations(allocationService.convertEmbeddableListToDTOList(event.getAssetAllocations()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void runInvestEvents(Scenario scenario, SimulationContext context) {
        // Citation: GPT helped to use map for event Schedule
        Map<Long, Pair<Integer,Integer>> investEventSchedule = context.getInvestEventSchedule();
        if (investEventSchedule == null) {
            throw new IllegalStateException("InvestEvent schedule not initialized in context");
        }

        int currentYear = context.getCurrentYear();
        List<InvestEvent> investEvents = currentYear == LocalDateTime.now().getYear()
                ? investEventRepository.findAllByEventSeries_Scenario_Id(scenario.getId())
                : context.getUpdatedInvestEvents();
        if (investEvents == null || investEvents.isEmpty()) {
            return;
        }

        // Prepare asset allocations and retain previous updatedInvestments
        List<Investment> updatedInvestments = context.getUpdatedInvestments();
        BigDecimal excessCash = context.getCashBalance();

        // Preload this year's allocations from schedule
        context.setAssetAllocations(new ArrayList<>());
        for (InvestEvent event : investEvents) {
            Pair<Integer,Integer> sched = investEventSchedule.get(event.getEventSeries().getId());
            if (sched == null) continue;
            int startYear = sched.getKey();
            int duration  = sched.getValue();
            int endYear   = startYear + duration;

            // Only include allocations if event is active this year
            if (currentYear >= startYear && currentYear < endYear) {
                context.getAssetAllocations().addAll(event.getAssetAllocations());
            }
        }

        // Build a map for current investments
        Map<String, Investment> investmentMap = context.getUpdatedInvestments().stream()
                .collect(Collectors.toMap(
                        inv -> inv.getInvestmentType().getName() + " " + inv.getTaxStatus(),
                        inv -> inv
                ));

        // Process each InvestEvent in schedule order
        for (InvestEvent event : investEvents) {
            Pair<Integer, Integer> sched = investEventSchedule.get(event.getEventSeries().getId());
            if (sched == null) continue;
            int startYear = sched.getKey();
            int duration = sched.getValue();
            int endYear = startYear + duration;

            // Skip if not active event
            if (currentYear < startYear || currentYear >= endYear) {
                continue;
            }

            // Stop if no cash
            if (excessCash.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }

            // Get maxCash amount from the event and limit the
            if (event.getMaxCash() != null) {
                BigDecimal eventMax = BigDecimal.valueOf(event.getMaxCash());
                if (excessCash.compareTo(eventMax) > 0) {
                    excessCash = eventMax;
                }
            }

            // Normalize allocation ratios
            List<AllocationEmbeddable> allocations = event.getAssetAllocations();
            BigDecimal totalRatio = allocations.stream()
                    .map(a -> BigDecimal.valueOf(a.getRatio()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Determine purchase amounts per allocation
            Map<AllocationEmbeddable, BigDecimal> toBuy = new LinkedHashMap<>();
            for (AllocationEmbeddable alloc : allocations) {
                BigDecimal pct = BigDecimal.valueOf(alloc.getRatio()).divide(totalRatio, MathContext.DECIMAL128);
                BigDecimal amount = excessCash.multiply(pct);
                toBuy.put(alloc, amount);
            }

            // Execute purchases
            BigDecimal spent = BigDecimal.ZERO;
            for (Map.Entry<AllocationEmbeddable, BigDecimal> entry : toBuy.entrySet()) {
                AllocationEmbeddable allocation = entry.getKey();
                BigDecimal amount = entry.getValue();
                if (amount.compareTo(BigDecimal.ZERO) <= 0) continue;
                spent = spent.add(amount);

                Investment investment = investmentMap.get(allocation.getInvestmentKey());
                BigDecimal newValue = BigDecimal.valueOf(investment.getValue()).add(amount);
                Investment updatedInvestment = investment.toBuilder()
                        .value(newValue.doubleValue())
                        .build();
                investmentMap.put(allocation.getInvestmentKey(), updatedInvestment);

                // Update the purchase price into Context
                List<Investment> priceRecords = context.getInvestmentsPurchasingPrices();
                boolean found = false;
                for (int i = 0; i < priceRecords.size(); i++) {
                    Investment record = priceRecords.get(i);
                    if (record.getId().equals(investment.getId())) {
                        BigDecimal updatedPrice = BigDecimal.valueOf(record.getValue()).add(amount);
                        Investment newRecord = record.toBuilder()
                                .value(updatedPrice.doubleValue())
                                .build();
                        priceRecords.set(i, newRecord);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    Investment newRecord = investment.toBuilder()
                            .value(amount.doubleValue())
                            .build();
                    priceRecords.add(newRecord);
                }

                // Calculate the excessCash and cashBalance
                excessCash = excessCash.subtract(amount);
                context.setCashBalance(excessCash);
            }

            // Save updated Investments into Context
            List<Investment> updatedList = new ArrayList<>(investmentMap.values());
            context.setUpdatedInvestments(updatedList);
        }
    }
}
