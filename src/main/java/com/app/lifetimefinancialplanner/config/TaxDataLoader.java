package com.app.lifetimefinancialplanner.config;

import com.app.lifetimefinancialplanner.domain.dto.FederalTaxDTO;
import com.app.lifetimefinancialplanner.domain.dto.StateTaxDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.Getter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Component
public class TaxDataLoader {

    @Getter
    private FederalTaxDTO federalTaxDTO;
    @Getter
    private StateTaxDTO stateTaxDTO;

    private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

    @PostConstruct
    public void init() throws IOException {
        // Load the YAML files from resources
        ClassPathResource federalResource = new ClassPathResource("static/federalTaxBracket2025.yaml");
        federalTaxDTO = yamlMapper.readValue(federalResource.getInputStream(), FederalTaxDTO.class);

        ClassPathResource stateResource = new ClassPathResource("static/stateTaxBracket2025.yaml");
        stateTaxDTO = yamlMapper.readValue(stateResource.getInputStream(), StateTaxDTO.class);
    }
}
