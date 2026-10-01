package com.divyesh.incomestatementanalysis.service.impl;

import com.divyesh.incomestatementanalysis.ai.BedrockPromptBuilder;
import com.divyesh.incomestatementanalysis.ai.BedrockRequest;
import com.divyesh.incomestatementanalysis.ai.BedrockResponse;
import com.divyesh.incomestatementanalysis.config.BedrockConfig;
import com.divyesh.incomestatementanalysis.exception.BedrockException;
import com.divyesh.incomestatementanalysis.model.IncomeStatement;
import com.divyesh.incomestatementanalysis.parser.JsonResponseParser;
import com.divyesh.incomestatementanalysis.service.BedrockService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BedrockServiceImpl implements BedrockService {

    private final BedrockRuntimeClient bedrockRuntimeClient;
    private final BedrockConfig bedrockConfig;
    private final BedrockPromptBuilder bedrockPromptBuilder;
    private final JsonResponseParser jsonResponseParser;
    private final ObjectMapper objectMapper;

    @Override
    public IncomeStatement analyzeIncomeStatement(String extractedText) {
        log.info("Initiating Bedrock income statement analysis.");

        try {
            // 1. Build the text prompt from OCR extracted input
            String promptText = bedrockPromptBuilder.buildPrompt(extractedText);

            // 2. Construct BedrockRequest with Anthropic Messages API format
            BedrockRequest requestPayload = BedrockRequest.builder()
                    .anthropicVersion("bedrock-2023-05-31")
                    .maxTokens(2000)
                    .temperature(0.0)
                    .messages(List.of(
                            BedrockRequest.Message.builder()
                                    .role("user")
                                    .content(List.of(
                                            BedrockRequest.Content.builder()
                                                    .type("text")
                                                    .text(promptText)
                                                    .build()
                                    ))
                                    .build()
                    ))
                    .build();

            // 3. Serialize request object to JSON
            String jsonPayload = objectMapper.writeValueAsString(requestPayload);

            // 4. Build AWS Bedrock Runtime InvokeModel request
            InvokeModelRequest invokeModelRequest = InvokeModelRequest.builder()
                    .modelId(bedrockConfig.getModelId())
                    .contentType("application/json")
                    .accept("application/json")
                    .body(SdkBytes.fromUtf8String(jsonPayload))
                    .build();

            // 5. Invoke AWS Bedrock Runtime Client
            InvokeModelResponse invokeModelResponse = bedrockRuntimeClient.invokeModel(invokeModelRequest);

            // 6. Extract raw JSON response payload
            String rawResponseBody = invokeModelResponse.body().asUtf8String();

            // 7. Deserialize raw response into BedrockResponse object
            BedrockResponse bedrockResponse = objectMapper.readValue(rawResponseBody, BedrockResponse.class);

            // 8. Safely extract content text from Claude response payload
            List<BedrockResponse.Content> contentList = bedrockResponse.getContent();
            if (contentList == null || contentList.isEmpty() || contentList.get(0).getText() == null) {
                throw new BedrockException("Received empty or malformed response content from AWS Bedrock.");
            }

            String jsonText = contentList.get(0).getText();

            // 9. Parse extracted JSON string into target IncomeStatement model
            return jsonResponseParser.parse(jsonText);

        } catch (BedrockException e) {
            // Re-throw domain exceptions directly without extra wrapping
            throw e;
        } catch (Exception e) {
            log.error("Failed to process income statement with AWS Bedrock.", e);
            throw new BedrockException("Failed to analyze income statement using AWS Bedrock.", e);
        }
    }
}