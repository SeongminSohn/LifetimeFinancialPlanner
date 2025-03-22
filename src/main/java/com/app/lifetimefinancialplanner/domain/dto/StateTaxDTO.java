package com.app.lifetimefinancialplanner.domain.dto;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Setter
@Getter
public class StateTaxDTO {
    // "stateBrackets": { "NY": {"SINGLE": [...], "MARRIED_JOINT": [...]}, "NJ": {...} }
    private Map<String, StateTaxData> stateBrackets;

    @Getter
    @Setter
    public static class StateTaxData {
        // List of tax brackets by filing status
        private Map<String, List<TaxBracketDTO>> brackets = new HashMap<>();
        // Standard deduction by filing status (SINGLE or MARRIED_JOINT)
        private Map<String, Object> standardDeduction;
        // Personal exemption by filing status
        private Map<String, Object> personalExemption;

        @JsonAnySetter
        public void handleUnknown(String key, Object value) {
            if ("StandardDeduction".equalsIgnoreCase(key)) {
                if (value instanceof Map) {
                    standardDeduction = (Map<String, Object>) value;
                }
            } else if ("PersonalExemption".equalsIgnoreCase(key)) {
                if (value instanceof Map) {
                    personalExemption = (Map<String, Object>) value;
                }
            } else {
                // For any other key (e.g., "SINGLE", "MARRIED_JOINT"), treat as tax bracket list.
                ObjectMapper mapper = new ObjectMapper();
                List<TaxBracketDTO> list = mapper.convertValue(value, new TypeReference<List<TaxBracketDTO>>() {});
                brackets.put(key, list);
            }
        }
    }
}
