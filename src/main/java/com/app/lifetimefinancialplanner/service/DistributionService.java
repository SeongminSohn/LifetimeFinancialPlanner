package com.app.lifetimefinancialplanner.service;

import com.app.lifetimefinancialplanner.domain.dto.DistributionDTO;
import com.app.lifetimefinancialplanner.domain.embeddable.DistributionEmbeddable;
import org.springframework.stereotype.Service;

@Service
public class DistributionService {

    // Helper Method to process DTO to Embeddable (Handle invalid inputs)
    DistributionEmbeddable convertDTOToEmbeddable(DistributionDTO dto) {
        if (dto == null || dto.getDistributionType() == null || dto.getDistributionType().trim().isEmpty()) {
            return null;
        }
        DistributionEmbeddable emb = new DistributionEmbeddable();
        emb.setAmountOrPercent(dto.getAmountOrPercent());
        emb.setDistributionType(dto.getDistributionType());
        String type = dto.getDistributionType();
        if ("FIXED".equalsIgnoreCase(type)) {
            emb.setValue(dto.getValue());
            emb.setLower(null);
            emb.setUpper(null);
            emb.setMean(null);
            emb.setStDev(null);
        } else if ("UNIFORM".equalsIgnoreCase(type)) {
            emb.setLower(dto.getLower());
            emb.setUpper(dto.getUpper());
            emb.setValue(null);
            emb.setMean(null);
            emb.setStDev(null);
        } else if ("NORMAL".equalsIgnoreCase(type)) {
            emb.setMean(dto.getMean());
            emb.setStDev(dto.getStDev());
            emb.setValue(null);
            emb.setLower(null);
            emb.setUpper(null);
        } else {
            throw new IllegalArgumentException("Unsupported distribution type: " + type);
        }
        return emb;
    }

    // Helper Method to process Embeddable to DTO
    public DistributionDTO convertEmbeddableToDTO(DistributionEmbeddable emb) {
        if (emb == null) {
            return null;
        }
        DistributionDTO dto = new DistributionDTO();
        dto.setAmountOrPercent(emb.getAmountOrPercent());
        dto.setDistributionType(emb.getDistributionType());
        dto.setValue(emb.getValue());
        dto.setLower(emb.getLower());
        dto.setUpper(emb.getUpper());
        dto.setMean(emb.getMean());
        dto.setStDev(emb.getStDev());
        return dto;
    }
}
