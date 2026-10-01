package com.divyesh.incomestatementanalysis.parser;

import com.divyesh.incomestatementanalysis.exception.BedrockException;
import com.divyesh.incomestatementanalysis.exception.OCRException;
import com.divyesh.incomestatementanalysis.model.IncomeStatement;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JsonResponseParser {

    private final ObjectMapper objectMapper;

    /**
     * Converts the JSON returned by Claude into
     * an IncomeStatement object.
     *
     * @param jsonResponse Claude JSON response
     * @return IncomeStatement
     */
    public IncomeStatement parse(String jsonResponse) {

        try {

            objectMapper.configure(
                    DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                    false
            );

            return objectMapper.readValue(
                    jsonResponse,
                    IncomeStatement.class
            );

        } catch (Exception exception) {

            log.error("Unable to parse Claude JSON response.", exception);

            throw new BedrockException(
                    "Invalid JSON received from AWS Bedrock.",
                    exception
            );

        }

    }

}