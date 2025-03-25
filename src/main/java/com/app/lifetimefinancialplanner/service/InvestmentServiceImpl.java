package com.app.lifetimefinancialplanner.service;

import com.app.lifetimefinancialplanner.domain.dto.InvestmentDTO;
import com.app.lifetimefinancialplanner.domain.entity.Investment;
import com.app.lifetimefinancialplanner.domain.entity.InvestmentType;
import com.app.lifetimefinancialplanner.domain.entity.Scenario;
import com.app.lifetimefinancialplanner.repository.InvestmentRepository;
import com.app.lifetimefinancialplanner.repository.InvestmentTypeRepository;
import com.app.lifetimefinancialplanner.repository.ScenarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InvestmentServiceImpl implements InvestmentService {

    private final InvestmentRepository investmentRepository;
    private final InvestmentTypeRepository investmentTypeRepository;
    private final ScenarioRepository scenarioRepository;
    private final DistributionService distributionService;

    public InvestmentServiceImpl(InvestmentRepository investmentRepository,
                                 InvestmentTypeRepository investmentTypeRepository,
                                 ScenarioRepository scenarioRepository,
                                 DistributionService distributionService) {
        this.investmentRepository = investmentRepository;
        this.investmentTypeRepository = investmentTypeRepository;
        this.scenarioRepository = scenarioRepository;
        this.distributionService = distributionService;
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

}
