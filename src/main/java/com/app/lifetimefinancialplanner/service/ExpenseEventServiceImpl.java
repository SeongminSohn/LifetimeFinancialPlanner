package com.app.lifetimefinancialplanner.service;

import com.app.lifetimefinancialplanner.domain.dto.DistributionDTO;
import com.app.lifetimefinancialplanner.domain.dto.ExpenseEventDTO;
import com.app.lifetimefinancialplanner.domain.embeddable.DistributionEmbeddable;
import com.app.lifetimefinancialplanner.domain.entity.EventSeries;
import com.app.lifetimefinancialplanner.domain.entity.ExpenseEvent;
import com.app.lifetimefinancialplanner.repository.EventSeriesRepository;
import com.app.lifetimefinancialplanner.repository.ExpenseEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExpenseEventServiceImpl implements ExpenseEventService {

    private final ExpenseEventRepository expenseEventRepository;
    private final EventSeriesRepository eventSeriesRepository;

    public ExpenseEventServiceImpl(ExpenseEventRepository expenseEventRepository,
                                   EventSeriesRepository eventSeriesRepository) {
        this.expenseEventRepository = expenseEventRepository;
        this.eventSeriesRepository = eventSeriesRepository;
    }

    @Override
    @Transactional
    public ExpenseEvent createExpenseEvent(ExpenseEventDTO expenseEventDTO) {
        // Retrieve the associated EventSeries using the provided eventSeriesId
        EventSeries eventSeries = eventSeriesRepository.findById(expenseEventDTO.getEventSeriesId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid EventSeries ID"));

        // Convert DistributionDTO for annualChange to DistributionEmbeddable
        DistributionEmbeddable annualChangeEmb = null;
        DistributionDTO distributionDTO = expenseEventDTO.getAnnualChange();
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

        // Build the ExpenseEvent entity using the builder pattern
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
}
