package com.app.lifetimefinancialplanner.service;

import com.app.lifetimefinancialplanner.domain.context.SimulationContext;
import com.app.lifetimefinancialplanner.domain.dto.InvestmentDTO;
import com.app.lifetimefinancialplanner.domain.entity.Investment;
import com.app.lifetimefinancialplanner.domain.entity.InvestmentType;
import com.app.lifetimefinancialplanner.domain.entity.Scenario;
import com.app.lifetimefinancialplanner.repository.InvestmentRepository;
import com.app.lifetimefinancialplanner.repository.InvestmentTypeRepository;
import com.app.lifetimefinancialplanner.repository.ScenarioRepository;
import com.app.lifetimefinancialplanner.domain.dto.InvestmentTypeDTO;
import com.app.lifetimefinancialplanner.domain.context.SimulationContext;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//import com.app.lifetimefinancialplanner.repository.EventSeriesRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;

@Service
public class InvestmentServiceImpl implements InvestmentService {

    private final InvestmentRepository investmentRepository;
    private final InvestmentTypeRepository investmentTypeRepository;
    private final ScenarioRepository scenarioRepository;
    private final DistributionService distributionService;
    private final SamplingService samplingService;
//    private final EventSeriesRepository eventSeriesRepository;


    public InvestmentServiceImpl(InvestmentRepository investmentRepository,
                                 InvestmentTypeRepository investmentTypeRepository,
                                 ScenarioRepository scenarioRepository
                ,SamplingService samplingService,DistributionService distributionService
    ) {
        this.investmentRepository = investmentRepository;
        this.investmentTypeRepository = investmentTypeRepository;
        this.scenarioRepository = scenarioRepository;
        this.distributionService = distributionService;
        this.samplingService = samplingService;
//        this.eventSeriesRepository = eventSeriesRepository;
    }

    @Override
    @Transactional
    public Investment createInvestment(InvestmentDTO dto) {
        InvestmentType investmentType = investmentTypeRepository.findById(dto.getInvestmentTypeId())
                .orElseThrow(() -> new RuntimeException("InvestmentType not found with id: " + dto.getInvestmentTypeId()));

        Scenario scenario = scenarioRepository.findById(dto.getScenarioId())
                .orElseThrow(() -> new RuntimeException("Scenario not found with id: " + dto.getScenarioId()));

        Investment investment = Investment.builder()
                .investmentType(investmentType)
                .value(dto.getValue())
                .taxStatus(dto.getTaxStatus())
                .scenario(scenario)
                .createdAt(LocalDateTime.now())
                .build();

        return investmentRepository.save(investment);
    }

    @Override
    public Optional<Investment> getInvestment(Long id) {
        return investmentRepository.findById(id);
    }

    @Override
    @Transactional
    public Investment updateInvestment(Long id, InvestmentDTO dto) {
        // Find existing Investment entity
        Investment existing = investmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Investment not found with id: " + id));

        Scenario scenario = existing.getScenario();
        if (dto.getScenarioId() != null && !dto.getScenarioId().equals(scenario.getId())) {
            scenario = scenarioRepository.findById(dto.getScenarioId())
                    .orElseThrow(() -> new RuntimeException("Scenario not found with id: " + dto.getScenarioId()));
        }

        Investment updated = existing.toBuilder()
                .value(dto.getValue() != null ? dto.getValue() : existing.getValue())
                .taxStatus(dto.getTaxStatus() != null ? dto.getTaxStatus() : existing.getTaxStatus())
                .scenario(scenario)
                .build();

        return investmentRepository.save(updated);
    }

    @Override
    @Transactional
    public void deleteInvestment(Long id) {
        Investment existing = investmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Investment not found with id: " + id));
        investmentRepository.delete(existing);
    }

    @Override
    public List<InvestmentDTO> getInvestmentListByScenarioId(Long scenarioId) {
        List<Investment> investments = investmentRepository.findAll().stream()
                .filter(investment -> investment.getScenario().getId().equals(scenarioId))
                .collect(Collectors.toList());

        return investments.stream()
                .map(entity -> {
                    InvestmentDTO investmentDTO = new InvestmentDTO();
                    investmentDTO.setId(entity.getId());
                    investmentDTO.setScenarioId(entity.getScenario().getId());
                    investmentDTO.setInvestmentTypeId(entity.getInvestmentType().getId());
                    investmentDTO.setValue(entity.getValue());
                    investmentDTO.setTaxStatus(entity.getTaxStatus());
                    return investmentDTO;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateInvestmentValues(Scenario scenario, SimulationContext context) {
        if (context.getCurrentYear() == LocalDateTime.now().getYear()) {
            List<Investment> investmentList = investmentRepository.findAllByScenarioId(scenario.getId());
            List<Investment> updatedInvestmentList = new ArrayList<>();
            if (investmentList == null || investmentList.isEmpty()) {
                throw new IllegalArgumentException("There is no Investment Information and This is Scenario ID: " + scenario.getId());
            }
            for (Investment investment : investmentList) {
                // 1. This is initial value
                BigDecimal initialValue = BigDecimal.valueOf(investment.getValue());
                // and this is initial generated Income
                BigDecimal generatedIncome = BigDecimal.ZERO;
                InvestmentType investType = investment.getInvestmentType();
                if (investType == null) {
                    throw new IllegalArgumentException("Can't find invest Info. investmentTypeId: " + investment.getInvestmentType());
                }
                generatedIncome = BigDecimal.valueOf((double) samplingService.sample(distributionService.convertEmbeddableToDTO(investType.getExpectedAnnualIncome())));
                generatedIncome = generatedIncome.multiply(BigDecimal.valueOf(context.getInflationFactor()));
                if ("NON-RETIREMENT".equalsIgnoreCase(investment.getTaxStatus()) && "Y".equalsIgnoreCase(investType.getTaxability())) {
                    context.setCurYearIncome(context.getCurYearIncome().add(generatedIncome));
                }

                BigDecimal valueChange = BigDecimal.valueOf(samplingService.sample(distributionService.convertEmbeddableToDTO(investType.getExpectedAnnualReturn())));
                BigDecimal newValue = initialValue.add(generatedIncome).add(valueChange);
                BigDecimal averageValue = initialValue.add(newValue).divide(BigDecimal.valueOf(2), MathContext.DECIMAL128);
                BigDecimal expense = BigDecimal.valueOf(investType.getExpenseRatio()).multiply(averageValue);
                BigDecimal finalValue = newValue.subtract(expense);
                Investment updatedInvestment = investment.toBuilder()
                        .value(finalValue.doubleValue())
                        .build();

                updatedInvestmentList.add(updatedInvestment);
            }
            context.setUpdatedInvestments(updatedInvestmentList);
        } else {
            List<Investment> investmentList = context.getUpdatedInvestments();
            List<Investment> updatedInvestmentList = new ArrayList<>();
            if (investmentList == null || investmentList.isEmpty()) {
                throw new IllegalArgumentException("There is no Investment Information and This is Scenario ID: " + scenario.getId());
            }
            for (Investment investment : investmentList) {
                // 1. This is initial value
                BigDecimal initialValue = BigDecimal.valueOf(investment.getValue());
                // and this is initial generated Income
                BigDecimal generatedIncome = BigDecimal.ZERO;
                InvestmentType investType = investment.getInvestmentType();
                if (investType == null) {
                    throw new IllegalArgumentException("Can't find invest Info. investmentTypeId: " + investment.getInvestmentType());
                }
                generatedIncome = BigDecimal.valueOf((double) samplingService.sample(distributionService.convertEmbeddableToDTO(investType.getExpectedAnnualIncome())));
                generatedIncome = generatedIncome.multiply(BigDecimal.valueOf(context.getInflationFactor()));
                if ("NON-RETIREMENT".equalsIgnoreCase(investment.getTaxStatus()) && "Y".equalsIgnoreCase(investType.getTaxability())) {
                    context.setCurYearIncome(context.getCurYearIncome().add(generatedIncome));
                }

                BigDecimal valueChange = BigDecimal.valueOf(samplingService.sample(distributionService.convertEmbeddableToDTO(investType.getExpectedAnnualReturn())));
                BigDecimal newValue = initialValue.add(generatedIncome).add(valueChange);
                BigDecimal averageValue = initialValue.add(newValue).divide(BigDecimal.valueOf(2), MathContext.DECIMAL128);
                BigDecimal expense = BigDecimal.valueOf(investType.getExpenseRatio()).multiply(averageValue);
                BigDecimal finalValue = newValue.subtract(expense);
                Investment updatedInvestment = investment.toBuilder()
                        .value(finalValue.doubleValue())
                        .build();
                updatedInvestmentList.add(updatedInvestment);
            }
            context.setUpdatedInvestments(updatedInvestmentList);
        }
    }
}
