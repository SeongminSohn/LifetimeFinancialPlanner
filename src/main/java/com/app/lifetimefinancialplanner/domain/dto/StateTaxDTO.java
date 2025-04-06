package com.app.lifetimefinancialplanner.domain.dto;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter @Setter
public class StateTaxDTO {
    // "stateBrackets": { "NY": {"SINGLE": [...], "MARRIED_JOINT": [...]}, "NJ": {...} }
    private Map<String, StateTaxData> stateBrackets;

    @Getter @Setter
    public static class StateTaxData {
        // Map of filing status to list of tax brackets.
        private Map<String, List<TaxBracketDTO>> brackets = new HashMap<>();

        // Citation: GPT helped me how to parse the state tax bracket
        @JsonAnySetter
        public void handleUnknown(String key, Object value) {
            // For keys like "SINGLE" or "MARRIED_JOINT", treat as tax bracket list.
            ObjectMapper mapper = new ObjectMapper();
            List<TaxBracketDTO> list = mapper.convertValue(value, new TypeReference<List<TaxBracketDTO>>() {});
            brackets.put(key, list);
        }
    }
}
