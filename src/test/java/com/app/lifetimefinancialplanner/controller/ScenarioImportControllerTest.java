package com.app.lifetimefinancialplanner.controller;

import com.app.lifetimefinancialplanner.domain.entity.Scenario;
import com.app.lifetimefinancialplanner.domain.embeddable.DistributionEmbeddable;
import com.app.lifetimefinancialplanner.service.ScenarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ScenarioController.class)
class ScenarioImportControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    ScenarioService scenarioService;

    @Test
    void importScenario_createsAndReturnsDto() throws Exception {
        String yaml = ""
                + "name: \"Imported\"\n"
                + "maritalStatus: couple\n"
                + "birthYears: [1980, 1982]\n"
                + "lifeExpectancy:\n"
                + "  - { type: fixed, value: 85 }\n"
                + "  - { type: fixed, value: 88 }\n"
                + "inflationAssumption: { type: fixed, value: 0.03 }\n"
                + "financialGoal: 50000\n"
                + "afterTaxContributionLimit: 7000\n"
                + "residenceState: NY\n";

        Scenario created = Scenario.builder()
                .id(2L)
                .name("Imported")
                .maritalStatus("couple")
                .birthYearUser(1980)
                .birthYearSpouse(1982)
                .financialGoal(50000.0)
                .afterTaxContributionLimit(7000.0)
                .stateOfResidence("NY")
                .inflationAssumption(new DistributionEmbeddable() {{ setDistributionType("FIXED"); setValue(0.03); }})
                .lifeExpectancyUser(new DistributionEmbeddable() {{ setDistributionType("FIXED"); setValue(85.0); }})
                .lifeExpectancySpouse(new DistributionEmbeddable() {{ setDistributionType("FIXED"); setValue(88.0); }})
                .build();

        when(scenarioService.importScenarioYaml(any(), any(Long.class))).thenReturn(null);
        when(scenarioService.createScenario(any())).thenReturn(created);

        mvc.perform(post("/api/scenarios/import")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .content(yaml))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.name").value("Imported"))
                .andExpect(jsonPath("$.birthYearUser").value(1980))
                .andExpect(jsonPath("$.birthYearSpouse").value(1982))
                .andExpect(jsonPath("$.stateOfResidence").value("NY"));
    }
}
