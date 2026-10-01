package com.divyesh.incomestatementanalysis.validation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class JsonValidator {

    private final ObjectMapper objectMapper;

    public JsonValidator(ObjectMapper objectMapper) {

        this.objectMapper = objectMapper;

    }

    public boolean isValid(String json) {

        try {

            JsonNode node = objectMapper.readTree(json);

            return node != null;

        }

        catch (Exception e) {

            return false;

        }

    }

}