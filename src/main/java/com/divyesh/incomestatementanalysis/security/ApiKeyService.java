package com.divyesh.incomestatementanalysis.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ApiKeyService {

    @Value("${app.security.api-key:default-secret-api-key}")
    private String configuredApiKey;

    /**
     * Validates if the provided API Key matches the configured system key.
     */
    public boolean isValidApiKey(String apiKey) {
        return apiKey != null && apiKey.equals(configuredApiKey);
    }
}