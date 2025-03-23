package com.app.lifetimefinancialplanner.service;

import com.app.lifetimefinancialplanner.domain.dto.DistributionDTO;
import com.app.lifetimefinancialplanner.domain.embeddable.DistributionEmbeddable;
import com.app.lifetimefinancialplanner.domain.entity.EventSeries;
import com.app.lifetimefinancialplanner.domain.entity.IncomeEvent;
import com.app.lifetimefinancialplanner.domain.dto.IncomeEventDTO;
import com.app.lifetimefinancialplanner.repository.EventSeriesRepository;
import com.app.lifetimefinancialplanner.repository.IncomeEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IncomeEventServiceImpl implements IncomeEventService {

    private final IncomeEventRepository incomeEventRepository;
    private final EventSeriesRepository eventSeriesRepository;

    public IncomeEventServiceImpl(IncomeEventRepository incomeEventRepository,
                                  EventSeriesRepository eventSeriesRepository) {
        this.incomeEventRepository = incomeEventRepository;
        this.eventSeriesRepository = eventSeriesRepository;
    }

    @Override
    @Transactional
    public IncomeEvent createIncomeEvent(IncomeEventDTO incomeEventDTO) {
        // Retrieve the associated EventSeries using the provided eventSeriesId
        EventSeries eventSeries = eventSeriesRepository.findById(incomeEventDTO.getEventSeriesId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid EventSeries ID"));

        // Convert DistributionDTO for annualChange to DistributionEmbeddable
        DistributionEmbeddable annualChangeEmb = null;
        DistributionDTO distributionDTO = incomeEventDTO.getAnnualChange();
        if (distributionDTO != null) {
            annualChangeEmb = new DistributionEmbeddable();
            annualChangeEmb.setAmountOrPercent(distributionDTO.getAmountOrPercent());
            annualChangeEmb.setDistributionType(distributionDTO.getDistributionType());
            annualChangeEmb.setValue(distributionDTO.getValue());
            annualChangeEmb.setLower(distributionDTO.getLower());
            annualChangeEmb.setUpper(distributionDTO.getUpper());
            annualChangeEmb.setMean(distributionDTO.getMean());
            annualChangeEmb.setStDev(distributionDTO.getStDev());
        }

        // Build the IncomeEvent entity using the builder pattern
        IncomeEvent incomeEvent = IncomeEvent.builder()
                .initialAmount(incomeEventDTO.getInitialAmount())
                .annualChange(annualChangeEmb)
                .isSocialSecurity(incomeEventDTO.getIsSocialSecurity())
                .userPercentage(incomeEventDTO.getUserPercentage())
                .eventSeries(eventSeries)
                .build();

        return incomeEventRepository.save(incomeEvent);
    }

    @Override
    public IncomeEvent getIncomeEvent(Long eventSeriesId) {
        return incomeEventRepository.findById(eventSeriesId)
                .orElseThrow(() -> new IllegalArgumentException("IncomeEvent not found with id: " + eventSeriesId));
    }
}
