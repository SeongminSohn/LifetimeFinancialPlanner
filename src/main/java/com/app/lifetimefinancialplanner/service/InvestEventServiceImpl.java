package com.app.lifetimefinancialplanner.service;

import com.app.lifetimefinancialplanner.domain.dto.InvestEventDTO;
import com.app.lifetimefinancialplanner.domain.entity.EventSeries;
import com.app.lifetimefinancialplanner.domain.entity.InvestEvent;
import com.app.lifetimefinancialplanner.repository.EventSeriesRepository;
import com.app.lifetimefinancialplanner.repository.InvestEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InvestEventServiceImpl implements InvestEventService {

    private final InvestEventRepository investEventRepository;
    private final EventSeriesRepository eventSeriesRepository;
    private final DistributionService distributionService;

    public InvestEventServiceImpl(InvestEventRepository investEventRepository,
                                  EventSeriesRepository eventSeriesRepository,
                                  DistributionService distributionService) {
        this.investEventRepository = investEventRepository;
        this.eventSeriesRepository = eventSeriesRepository;
        this.distributionService = distributionService;
    }

    @Override
    @Transactional
    public InvestEvent createInvestEvent(InvestEventDTO investEventDTO) {
        // Retrieve the associated EventSeries using eventSeriesId from the DTO.
        EventSeries eventSeries = eventSeriesRepository.findById(investEventDTO.getEventSeriesId())
                .orElseThrow(() -> new RuntimeException("EventSeries not found with id: " + investEventDTO.getEventSeriesId()));

        InvestEvent investEvent = InvestEvent.builder()
                .assetAllocation(distributionService.convertDTOToEmbeddable(investEventDTO.getAssetAllocation()))
                .maxCash(investEventDTO.getMaxCash())
                .eventSeries(eventSeries)
                .build();
        return investEventRepository.save(investEvent);
    }

    @Override
    public InvestEvent getInvestEvent(Long eventSeriesId) {
        return investEventRepository.findById(eventSeriesId)
                .orElseThrow(() -> new RuntimeException("InvestEvent not found with id: " + eventSeriesId));
    }

    @Override
    @Transactional
    public InvestEvent updateInvestEvent(Long eventSeriesId, InvestEventDTO investEventDTO) {
        InvestEvent existing = investEventRepository.findById(eventSeriesId)
                .orElseThrow(() -> new RuntimeException("InvestEvent not found with id: " + eventSeriesId));
        InvestEvent updated = existing.toBuilder()
                .assetAllocation(investEventDTO.getAssetAllocation() != null
                        ? distributionService.convertDTOToEmbeddable(investEventDTO.getAssetAllocation())
                        : existing.getAssetAllocation())
                .maxCash(investEventDTO.getMaxCash() != null ? investEventDTO.getMaxCash() : existing.getMaxCash())
                .build();
        return investEventRepository.save(updated);
    }

    @Override
    @Transactional
    public void deleteInvestEvent(Long eventSeriesId) {
        InvestEvent existing = investEventRepository.findById(eventSeriesId)
                .orElseThrow(() -> new RuntimeException("InvestEvent not found with id: " + eventSeriesId));
        investEventRepository.delete(existing);
    }
}
