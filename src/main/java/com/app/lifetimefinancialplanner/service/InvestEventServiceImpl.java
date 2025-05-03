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
        int currentYear = context.getCurrentYear();
        List<InvestEvent> investEvents = currentYear == LocalDateTime.now().getYear()
                ? investEventRepository.findAllByEventSeries_Scenario_Id(scenario.getId())
                : context.getUpdatedInvestEvents();
        if (investEvents == null || investEvents.isEmpty()) return;

        BigDecimal inflationFactor = BigDecimal.valueOf(context.getInflationFactor());
        BigDecimal excessCash = context.getCashBalance();

        List<Investment> investments = investmentRepository.findAllByScenarioId(scenario.getId());
        Map<String, Investment> invMap = investments.stream()
                .collect(Collectors.toMap(
                        inv -> inv.getInvestmentType().getName() + " " + inv.getTaxStatus(),
                        inv -> inv
                ));

        List<InvestEvent> updatedEvents = new ArrayList<>();
        for (InvestEvent event : investEvents) {
            int scheduledYear = (int) samplingService.sample(
                    distributionService.convertEmbeddableToDTO(event.getEventSeries().getStartYear())
            );
            if (scheduledYear != currentYear) continue;
            if (excessCash.compareTo(BigDecimal.ZERO) <= 0) break;

            List<AllocationEmbeddable> allocations = event.getAssetAllocations();
            context.setAssetAllocations(allocations);
            BigDecimal totalRatio = allocations.stream()
                    .map(a -> BigDecimal.valueOf(a.getRatio()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Map<AllocationEmbeddable, BigDecimal> toBuy = new LinkedHashMap<>();
            BigDecimal B = BigDecimal.ZERO;
            for (AllocationEmbeddable alloc : allocations) {
                BigDecimal pct = BigDecimal.valueOf(alloc.getRatio())
                        .divide(totalRatio, MathContext.DECIMAL128);
                BigDecimal amount = excessCash.multiply(pct);
                toBuy.put(alloc, amount);
                if (alloc.getInvestmentKey().endsWith("AFTER-TAX-RETIREMENT")) {
                    B = B.add(amount);
                }
            }

            BigDecimal L = BigDecimal.valueOf(context.getAdjustedAfterTaxContributionLimit())
                    .multiply(inflationFactor);
            if (B.compareTo(L) > 0) {
                BigDecimal downFactor = L.divide(B, MathContext.DECIMAL128);
                BigDecimal nonRetTotal = excessCash.subtract(B);
                BigDecimal upFactor = nonRetTotal.compareTo(BigDecimal.ZERO) > 0
                        ? excessCash.subtract(L).divide(nonRetTotal, MathContext.DECIMAL128)
                        : BigDecimal.ZERO;
                for (AllocationEmbeddable alloc : allocations) {
                    BigDecimal amt = toBuy.get(alloc);
                    amt = alloc.getInvestmentKey().endsWith("AFTER-TAX-RETIREMENT")
                            ? amt.multiply(downFactor)
                            : amt.multiply(upFactor);
                    toBuy.put(alloc, amt);
                }
            }

            BigDecimal spent = BigDecimal.ZERO;
            for (Map.Entry<AllocationEmbeddable, BigDecimal> e : toBuy.entrySet()) {
                AllocationEmbeddable alloc = e.getKey();
                BigDecimal amount = e.getValue();
                spent = spent.add(amount);
                Investment inv = invMap.get(alloc.getInvestmentKey());
                if (inv == null) {
                    throw new IllegalArgumentException("No Investment for key " + alloc.getInvestmentKey());
                }
                Investment updated = inv.toBuilder()
                        .value(inv.getValue() + amount.doubleValue())
                        .build();
                investmentRepository.save(updated);
            }

            excessCash = excessCash.subtract(spent);
            updatedEvents.add(event);
        }

        context.setCashBalance(excessCash);
        context.setUpdatedInvestEvents(updatedEvents);
    }
}
