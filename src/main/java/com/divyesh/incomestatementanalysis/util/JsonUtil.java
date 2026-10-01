package com.divyesh.incomestatementanalysis.util;

import com.divyesh.incomestatementanalysis.exception.BedrockException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JsonUtil {

    private final ObjectMapper objectMapper;

    /**
     * Convert Java Object to JSON String.
     *
     * @param object Java Object
     * @return JSON String
     */
    public String toJson(Object object) {

        try {

            return objectMapper.writeValueAsString(object);

        } catch (JsonProcessingException exception) {

            throw new BedrockException(
                    "Failed to convert object to JSON.",
                    exception
            );

        }

    }

    /**
     * Convert JSON String to Java Object.
     *
     * @param json JSON String
     * @param clazz Target Class
     * @param <T> Generic Type
     * @return Java Object
     */
    public <T> T fromJson(String json, Class<T> clazz) {

        try {

            return objectMapper.readValue(
                    json,
                    clazz
            );

        } catch (JsonProcessingException exception) {

            throw new BedrockException(
                    "Failed to convert JSON to Object.",
                    exception
            );

        }

    }

    /**
     * Pretty Print JSON.
     *
     * @param object Java Object
     * @return Formatted JSON
     */
    public String prettyPrint(Object object) {

        try {

            return objectMapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(object);

        } catch (JsonProcessingException exception) {

            throw new BedrockException(
                    "Failed to pretty print JSON.",
                    exception
            );

        }

    }

}