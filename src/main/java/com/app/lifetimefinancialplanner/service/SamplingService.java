package com.app.lifetimefinancialplanner.service;

import com.app.lifetimefinancialplanner.domain.dto.DistributionDTO;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Service
public class SamplingService {

    public double sample(DistributionDTO distributionDTO) {
        if (distributionDTO == null || distributionDTO.getDistributionType() == null) {
            throw new IllegalArgumentException("DistributionDTO or distributionType is null");
        }
        String type = distributionDTO.getDistributionType().toUpperCase();
        switch (type) {
            case "FIXED":
                return distributionDTO.getValue() != null ? distributionDTO.getValue() : 0;
            case "UNIFORM":
                if (distributionDTO.getLower() != null && distributionDTO.getUpper() != null) {
                    return ThreadLocalRandom.current().nextDouble(distributionDTO.getLower(), distributionDTO.getUpper());
                } else {
                    throw new IllegalArgumentException("Uniform distribution requires lower and upper bounds");
                }
            case "NORMAL":
                if (distributionDTO.getMean() != null && distributionDTO.getStDev() != null) {
                    // Use nextGaussian() (returns a standard normal value: mean=0, stDev=1)
                    double gaussian = ThreadLocalRandom.current().nextGaussian();
                    // Return scaled mean value by using input mean & stDev
                    return distributionDTO.getMean() + distributionDTO.getStDev() * gaussian;
                } else {
                    throw new IllegalArgumentException("Normal distribution requires mean and stDev");
                }
            default:
                throw new IllegalArgumentException("Unsupported distribution type: " + type);
        }
    }
}
