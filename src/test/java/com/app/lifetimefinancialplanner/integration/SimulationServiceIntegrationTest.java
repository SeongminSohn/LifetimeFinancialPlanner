package com.app.lifetimefinancialplanner.integration;

import com.app.lifetimefinancialplanner.domain.dto.SimulationDTO;
import com.app.lifetimefinancialplanner.domain.entity.Scenario;
import com.app.lifetimefinancialplanner.repository.ScenarioRepository;
import com.app.lifetimefinancialplanner.service.SimulationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {"logging.file.name=simulation-output.log"})
public class SimulationServiceIntegrationTest {
    @Autowired
    private SimulationService simulationService;

    @Autowired
    private ScenarioRepository scenarioRepository;

    @TempDir
    Path tempDir;

    @Test
    public void testRunSimulationWithCount10() throws Exception {
        Scenario scenario = scenarioRepository.findById(1L).orElseThrow();
        List<SimulationDTO> simulationDTO = simulationService.runSimulation(scenario.getId(), 10);
        assertNotNull(simulationDTO);
//        assertEquals(10, simulationDTO.getSimulationCount());
        Path outputFile = tempDir.resolve("simulation-output.log");
        assertTrue(Files.exists(outputFile));
        System.out.println(simulationDTO);
    }
}
