package com.app.lifetimefinancialplanner.service;

import com.app.lifetimefinancialplanner.domain.dto.InvestmentTypeDTO;
import com.app.lifetimefinancialplanner.domain.embeddable.DistributionEmbeddable;
import com.app.lifetimefinancialplanner.domain.entity.InvestmentType;
import com.app.lifetimefinancialplanner.repository.InvestmentTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InvestmentTypeServiceImpl implements InvestmentTypeService {

    private final InvestmentTypeRepository investmentTypeRepository;
    private final DistributionService distributionService;

    public InvestmentTypeServiceImpl(InvestmentTypeRepository investmentTypeRepository,
                                     DistributionService distributionService) {
        this.investmentTypeRepository = investmentTypeRepository;
        this.distributionService = distributionService;
    }

    @Override
    @Transactional
    public InvestmentType createInvestmentType(InvestmentTypeDTO investmentTypeDTO) {

        DistributionEmbeddable expectedAnnualReturnEmb = distributionService.convertDTOToEmbeddable(investmentTypeDTO.getExpectedAnnualReturn());
        DistributionEmbeddable expectedAnnualIncomeEmb = distributionService.convertDTOToEmbeddable(investmentTypeDTO.getExpectedAnnualIncome());

        InvestmentType investmentType = InvestmentType.builder()
                .name(investmentTypeDTO.getName())
                .description(investmentTypeDTO.getDescription())
                .expectedAnnualReturn(expectedAnnualReturnEmb)
                .expenseRatio(investmentTypeDTO.getExpenseRatio())
                .expectedAnnualIncome(expectedAnnualIncomeEmb)
                .taxability(investmentTypeDTO.getTaxability())
                .createdAt(LocalDateTime.now())
                .build();
        return investmentTypeRepository.save(investmentType);
    }

    @Override
    public Optional<InvestmentType> getInvestmentType(Long id) {
        return investmentTypeRepository.findById(id);
    }

    @Override
    @Transactional
    public InvestmentType updateInvestmentType(Long id, InvestmentTypeDTO investmentTypeDTO) {
        InvestmentType existing = investmentTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("InvestmentType not found"));

        DistributionEmbeddable expectedAnnualReturnEmb = distributionService.convertDTOToEmbeddable(investmentTypeDTO.getExpectedAnnualReturn());
        DistributionEmbeddable expectedAnnualIncomeEmb = distributionService.convertDTOToEmbeddable(investmentTypeDTO.getExpectedAnnualIncome());

        InvestmentType updated = existing.toBuilder()
                .name(investmentTypeDTO.getName() != null ? investmentTypeDTO.getName() : existing.getName())
                .description(investmentTypeDTO.getDescription() != null ? investmentTypeDTO.getDescription() : existing.getDescription())
                .expectedAnnualReturn(investmentTypeDTO.getExpectedAnnualReturn() != null ?
                        expectedAnnualReturnEmb : existing.getExpectedAnnualReturn())
                .expenseRatio(investmentTypeDTO.getExpenseRatio() != null ? investmentTypeDTO.getExpenseRatio() : existing.getExpenseRatio())
                .expectedAnnualIncome(investmentTypeDTO.getExpectedAnnualIncome() != null ?
                        expectedAnnualIncomeEmb : existing.getExpectedAnnualIncome())
                .taxability(investmentTypeDTO.getTaxability() != null ? investmentTypeDTO.getTaxability() : existing.getTaxability())
                .build();
        return investmentTypeRepository.save(updated);
    }

    @Override
    @Transactional
    public void deleteInvestmentType(Long id) {
        InvestmentType existing = investmentTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("InvestmentType not found"));
        investmentTypeRepository.delete(existing);
    }

    @Override
    public List<InvestmentTypeDTO> getInvestmentTypeList(Long scenarioId) {
        List<InvestmentType> types = investmentTypeRepository.findAllByScenarioId(scenarioId);

        return types.stream()
                .map(entity -> {
                    InvestmentTypeDTO investmentTypeDTO = new InvestmentTypeDTO();
                    investmentTypeDTO.setId(entity.getId());
                    investmentTypeDTO.setScenarioId(scenarioId);
                    investmentTypeDTO.setName(entity.getName());
                    investmentTypeDTO.setDescription(entity.getDescription());
                    investmentTypeDTO.setExpenseRatio(entity.getExpenseRatio());
                    investmentTypeDTO.setTaxability(entity.getTaxability());

                    // DistributionEmbeddable -> DistributionDTO
                    if (entity.getExpectedAnnualReturn() != null) {
                        investmentTypeDTO.setExpectedAnnualReturn(distributionService.convertEmbeddableToDTO(entity.getExpectedAnnualReturn()));
                    }
                    if (entity.getExpectedAnnualIncome() != null) {
                        investmentTypeDTO.setExpectedAnnualIncome(distributionService.convertEmbeddableToDTO(entity.getExpectedAnnualIncome()));
                    }
                    return investmentTypeDTO;
                })
                .collect(Collectors.toList());
    }
}
