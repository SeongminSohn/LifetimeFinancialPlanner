package com.app.lifetimefinancialplanner.service;

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
    private final DistributionService distributionService;

    public ExpenseEventServiceImpl(ExpenseEventRepository expenseEventRepository,
                                   EventSeriesRepository eventSeriesRepository, DistributionService distributionService) {
        this.expenseEventRepository = expenseEventRepository;
        this.eventSeriesRepository = eventSeriesRepository;
        this.distributionService = distributionService;
    }

    @Override
    @Transactional
    public ExpenseEvent createExpenseEvent(ExpenseEventDTO expenseEventDTO) {
        // TODO: Event SeriesId를 먼저 생성하는 service를 돌리고 그 아이디 담아서 돌아가야됌

        // Retrieve the associated EventSeries using the provided eventSeriesId
        EventSeries eventSeries = eventSeriesRepository.findById(expenseEventDTO.getEventSeriesId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid EventSeries ID"));

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
}
