package com.app.lifetimefinancialplanner.controller;

import com.app.lifetimefinancialplanner.service.ScenarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ScenarioController.class)
class ScenarioExportControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    ScenarioService scenarioService;

    @Test
    void exportScenario_returnsYamlResource() throws Exception {
        String yaml = ""
                + "name: My Scenario\n"
                + "maritalStatus: N\n"
                + "birthYears:\n"
                + "  - 1990\n"
                + "financialGoal: 10000.0\n"
                + "afterTaxContributionLimit: 7000.0\n"
                + "stateOfResidence: NY\n";
        Resource resource = new ByteArrayResource(yaml.getBytes(StandardCharsets.UTF_8));
        when(scenarioService.exportScenarioYaml(1L)).thenReturn(resource);

        mvc.perform(get("/api/scenarios/1/export"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/x-yaml"))
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"scenario-1.yaml\""))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("name: My Scenario")));
    }
}
