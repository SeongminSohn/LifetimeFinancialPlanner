package com.app.lifetimefinancialplanner.service;

import com.app.lifetimefinancialplanner.domain.dto.InflationAssumptionDTO;
import com.app.lifetimefinancialplanner.domain.entity.InflationAssumption;
import com.app.lifetimefinancialplanner.repository.InflationAssumptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InflationAssumptionService {

    private final InflationAssumptionRepository inflationAssumptionRepository;

    // Creates a new InflationAssumption entity
    public InflationAssumption createInflationAssumption(InflationAssumptionDTO inflationAssumptionDTO) {
        InflationAssumption entity = InflationAssumption.builder()
                .distributionType(inflationAssumptionDTO.getDistributionType())
                .fixedRate(inflationAssumptionDTO.getFixedRate())
                .minValue(inflationAssumptionDTO.getMinValue())
                .maxValue(inflationAssumptionDTO.getMaxValue())
                .mean(inflationAssumptionDTO.getMean())
                .stdDev(inflationAssumptionDTO.getStdDev())
                .build();

        return inflationAssumptionRepository.save(entity);
    }

    // Updates the InflationAssumption entity with new data
    public InflationAssumption updateInflationAssumption(Long id, InflationAssumptionDTO inflationAssumptionDTO) {
        Optional<InflationAssumption> optional = inflationAssumptionRepository.findById(id);
        if (optional.isEmpty()) {
            throw new RuntimeException("InflationAssumption not found: " + id);
        }
        InflationAssumption existing = optional.get();
        InflationAssumption updated = existing.toBuilder()
                .distributionType(inflationAssumptionDTO.getDistributionType())
                .fixedRate(inflationAssumptionDTO.getFixedRate())
                .minValue(inflationAssumptionDTO.getMinValue())
                .maxValue(inflationAssumptionDTO.getMaxValue())
                .mean(inflationAssumptionDTO.getMean())
                .stdDev(inflationAssumptionDTO.getStdDev())
                .build();

        return inflationAssumptionRepository.save(updated);
    }

    // Get InflationAssumption entity by its ID
    public InflationAssumption getInflationAssumption(Long id) {
        return inflationAssumptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("InflationAssumption not found: " + id));
    }

    // Delete InflationAssumption entity by its ID
    public void deleteInflationAssumption(Long id) {
        if (!inflationAssumptionRepository.existsById(id)) {
            throw new RuntimeException("InflationAssumption not found: " + id);
        }
        inflationAssumptionRepository.deleteById(id);
    }
}
