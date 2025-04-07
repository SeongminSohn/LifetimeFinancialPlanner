package com.app.lifetimefinancialplanner.service;

import com.app.lifetimefinancialplanner.domain.dto.SimulationDTO;
import com.app.lifetimefinancialplanner.domain.dto.SimulationYearDTO;
import com.app.lifetimefinancialplanner.domain.entity.Simulation;
import com.app.lifetimefinancialplanner.domain.entity.SimulationYear;
import com.app.lifetimefinancialplanner.repository.SimulationRepository;
import com.app.lifetimefinancialplanner.repository.SimulationYearRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SimulationServiceImpl implements SimulationService {

    private final SimulationRepository simulationRepository;
    private final SimulationYearRepository simulationYearRepository;

    public SimulationServiceImpl(SimulationRepository simulationRepository, SimulationYearRepository simulationYearRepository) {
        this.simulationRepository = simulationRepository;
        this.simulationYearRepository = simulationYearRepository;
    }

    @Override
    @Transactional
    public SimulationDTO runSimulation(Long scenarioId, Integer simulationCount) {

        List<SimulationYearDTO> simulationYearDTOList = new ArrayList<>();
        int startYear = LocalDateTime.now().getYear();
        int numYears = 40;
        for (int i = 0; i < numYears; i++) {
            int currentYear = startYear + i;

//            SimulationYear simulationYear = SimulationYear.builder()
//                    .simulation(simulation)
//                    .year(currentYear)
//                    .totalInvestments(totalInvestments)
//                    .totalIncome(totalIncome)
//                    .totalExpenses(totalExpenses)
//                    .totalTax(totalTax)
//                    .details(details)
//                    .build();
//            simulationYear = simulationYearRepository.save(simulationYear);
//
//            // Convert SimulationYear entity to DTO
//            SimulationYearDTO yearDTO = new SimulationYearDTO();
//            yearDTO.setId(simulationYear.getId());
//            yearDTO.setYear(simulationYear.getYear());
//            yearDTO.setTotalInvestments(simulationYear.getTotalInvestments());
//            yearDTO.setTotalIncome(simulationYear.getTotalIncome());
//            yearDTO.setTotalExpenses(simulationYear.getTotalExpenses());
//            yearDTO.setTotalTax(simulationYear.getTotalTax());
//            yearDTO.setDetails(simulationYear.getDetails());
//            yearDTO.setCreatedAt(simulationYear.getCreatedAt());
//
//            simulationYearDTOList.add(yearDTO);
        }

        // Create and save a Simulation entity
        Simulation simulation = Simulation.builder()
                .scenario(null)
                .simulationCount(simulationCount)
                .result("Simulation completed")
                .build();
        simulation = simulationRepository.save(simulation);

        // Build SimulationDTO
        SimulationDTO simulationDTO = new SimulationDTO();
        simulationDTO.setId(simulation.getId());
        simulationDTO.setScenarioId(simulation.getScenario().getId());
        simulationDTO.setSimulationCount(simulation.getSimulationCount());
        simulationDTO.setResult(simulation.getResult());
        simulationDTO.setCreatedAt(simulation.getCreatedAt());
        simulationDTO.setSimulationYears(simulationYearDTOList);

        return simulationDTO;
    }
}
