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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
        InvestEvent existing = investEventRepository.findById(eventSeriesId)
                .orElseThrow(() -> new RuntimeException("InvestEvent not found with id: " + eventSeriesId));
        log.info("Updating InvestEvent with id: " + eventSeriesId);
        log.info("Existing InvestEvent: " + existing);

        // Update assetAllocations and maxCash.
        List<AllocationEmbeddable> updatedAllocations =
                dto.getAssetAllocations() != null ?
                        allocationService.convertDTOListToEmbeddableList(dto.getAssetAllocations()) : existing.getAssetAllocations();
        log.info("Updated Allocation List:" + updatedAllocations);

        InvestEvent updated = existing.toBuilder()
                .maxCash(dto.getMaxCash() != null ? dto.getMaxCash() : existing.getMaxCash())
                .assetAllocations(updatedAllocations)
                .build();
        log.info("Updated InvestEvent: " + updated);

        return investEventRepository.save(updated);
    }

    @Override
    @Transactional
    public void deleteInvestEvent(Long eventSeriesId) {
        InvestEvent existing = investEventRepository.findById(eventSeriesId)
                .orElseThrow(() -> new RuntimeException("InvestEvent not found with id: " + eventSeriesId));
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
    public void runInvestEvents(Scenario scenario, SimulationContext context, Boolean userAlive, Boolean spouseAlive){
        int currentYear = context.getCurrentYear();
        List<InvestEvent> investEventList;
        List<InvestEvent> updatedInvestEventList = new ArrayList<>();

        if (context.getUpdatedInvestEvents() == null || context.getUpdatedInvestEvents().isEmpty()) {
            log.info("▶ runInvestEvents: loading from DB for Scenario ID={}", scenario.getId());
            investEventList = investEventRepository.findAllByEventSeries_Scenario_Id(scenario.getId());
            if (investEventList.isEmpty()) {
                log.warn("No InvestEvent data found for Scenario ID={}", scenario.getId());
                return;
            }
        } else {
            log.info("▶ runInvestEvents: using updated events from context for Scenario ID={}", scenario.getId());
            investEventList = context.getUpdatedInvestEvents();
        }

        for (InvestEvent ie : investEventList) {
            EventSeries es = ie.getEventSeries();
            // startYear, duration 샘플링
            int startYear  = (int) samplingService.sample(distributionService.convertEmbeddableToDTO(es.getStartYear()));
            int duration   = (int) samplingService.sample(distributionService.convertEmbeddableToDTO(es.getDuration()));
            int endYear    = startYear + duration;

            log.info("InvestEvent ID={} start={}, duration={}, end={}, simYear={}",
                    ie.getEventSeriesId(), startYear, duration, endYear, currentYear);

            if (currentYear >= startYear && currentYear < endYear) {
                BigDecimal cashBalance = context.getCashBalance();
                BigDecimal maxCash     = BigDecimal.valueOf(ie.getMaxCash());
                BigDecimal excessCash  = cashBalance.subtract(maxCash);

                if (excessCash.compareTo(BigDecimal.ZERO) > 0) {
                    Map<Investment, BigDecimal> purchaseMap = new HashMap<>();
                    double totalNonRetPct = 0.0;
                    for (AllocationEmbeddable alloc : ie.getAssetAllocations()) {
                        Investment inv = investmentRepository.findByScenarioAndNameAndTaxStatus(
                                        scenario, alloc.getAssetName(), alloc.getTaxStatus())
                                .orElseThrow(() -> new IllegalArgumentException(
                                        "Investment not found: " + alloc.getAssetName()));
                        BigDecimal pct  = BigDecimal.valueOf(alloc.getPercentage()).divide(BigDecimal.valueOf(100));
                        BigDecimal amt  = excessCash.multiply(pct);
                        purchaseMap.put(inv, amt);

                        if (inv.getTaxStatus() == TaxStatus.NON_RETIREMENT) {
                            totalNonRetPct += alloc.getPercentage();
                        }
                    }

                    double baseLimit      = scenario.getAfterTaxContributionLimit();
                    double inflationRate  = context.getInflationFactor();
                    int yearsSince        = currentYear - context.getCurrentYear();
                    BigDecimal L = BigDecimal.valueOf(baseLimit)
                            .multiply(BigDecimal.valueOf(Math.pow(1 + inflationRate, yearsSince)));
                    BigDecimal B = purchaseMap.entrySet().stream()
                            .filter(e -> e.getKey().getTaxStatus() == TaxStatus.AFTER_TAX)
                            .map(Map.Entry::getValue)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                }
            }


    }

}
