package com.divyesh.incomestatementanalysis.service.impl;

import com.divyesh.incomestatementanalysis.ai.BedrockPromptBuilder;
import com.divyesh.incomestatementanalysis.ai.BedrockResponse;
import com.divyesh.incomestatementanalysis.config.BedrockConfig;
import com.divyesh.incomestatementanalysis.exception.BedrockException;
import com.divyesh.incomestatementanalysis.model.IncomeStatement;
import com.divyesh.incomestatementanalysis.parser.JsonResponseParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BedrockServiceImplTest {

    @Mock
    private BedrockRuntimeClient bedrockRuntimeClient;

    @Mock
    private BedrockConfig bedrockConfig;

    @Mock
    private BedrockPromptBuilder bedrockPromptBuilder;

    @Mock
    private JsonResponseParser jsonResponseParser;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private BedrockServiceImpl bedrockService;

    private IncomeStatement incomeStatement;

    @BeforeEach
    void setUp() {
        incomeStatement = IncomeStatement.builder()
                .companyName("ABC Pvt Ltd")
                .financialYear("2024")
                .currency("INR")
                .build();
    }

    /**
     * Helper method to create a mocked AWS Bedrock InvokeModelResponse.
     */
    private InvokeModelResponse createInvokeResponse(String responseJson) {
        return InvokeModelResponse.builder()
                .body(SdkBytes.fromUtf8String(responseJson))
                .build();
    }

    /**
     * Helper method to create a mocked Claude response.
     */
    private BedrockResponse createClaudeResponse(String jsonText) {
        BedrockResponse.Content content = BedrockResponse.Content.builder()
                .type("text")
                .text(jsonText)
                .build();

        return BedrockResponse.builder()
                .content(List.of(content))
                .build();
    }

    @Test
    @DisplayName("Should return IncomeStatement when AWS Bedrock invocation succeeds")
    void analyzeIncomeStatement_ShouldReturnIncomeStatement_WhenBedrockSucceeds() throws Exception {
        // Arrange
        String extractedText = "Revenue 100000";
        String prompt = "Analyze this income statement";
        String requestJson = "{\"request\":\"json\"}";
        String responseJson = "{\"content\":[{\"type\":\"text\",\"text\":\"{\\\"companyName\\\":\\\"ABC Pvt Ltd\\\"}\"}]}";

        when(bedrockPromptBuilder.buildPrompt(extractedText)).thenReturn(prompt);
        when(bedrockConfig.getModelId()).thenReturn("anthropic.claude-3-haiku-20240307-v1:0");
        when(objectMapper.writeValueAsString(any())).thenReturn(requestJson);

        InvokeModelResponse invokeModelResponse = createInvokeResponse(responseJson);
        when(bedrockRuntimeClient.invokeModel(any(InvokeModelRequest.class))).thenReturn(invokeModelResponse);

        BedrockResponse bedrockResponse = createClaudeResponse("{\"companyName\":\"ABC Pvt Ltd\"}");
        when(objectMapper.readValue(eq(responseJson), eq(BedrockResponse.class))).thenReturn(bedrockResponse);
        when(jsonResponseParser.parse(anyString())).thenReturn(incomeStatement);

        // Act
        IncomeStatement result = bedrockService.analyzeIncomeStatement(extractedText);

        // Assert
        assertNotNull(result);
        assertEquals("ABC Pvt Ltd", result.getCompanyName());
        assertEquals("2024", result.getFinancialYear());
        assertEquals("INR", result.getCurrency());

        verify(bedrockPromptBuilder, times(1)).buildPrompt(extractedText);
        verify(bedrockConfig, times(1)).getModelId();
        verify(objectMapper, times(1)).writeValueAsString(any());
        verify(bedrockRuntimeClient, times(1)).invokeModel(any(InvokeModelRequest.class));
        verify(objectMapper, times(1)).readValue(eq(responseJson), eq(BedrockResponse.class));
        verify(jsonResponseParser, times(1)).parse(anyString());
    }

    @Test
    @DisplayName("Should throw BedrockException when response content list is empty")
    void analyzeIncomeStatement_ShouldThrowBedrockException_WhenResponseContentIsEmpty() throws Exception {
        // Arrange
        String extractedText = "Revenue 100000";
        String prompt = "Analyze this income statement";
        String requestJson = "{\"request\":\"json\"}";
        String responseJson = "{\"content\":[]}";

        when(bedrockPromptBuilder.buildPrompt(extractedText)).thenReturn(prompt);
        when(bedrockConfig.getModelId()).thenReturn("anthropic.claude-3-haiku-20240307-v1:0");
        when(objectMapper.writeValueAsString(any())).thenReturn(requestJson);
        when(bedrockRuntimeClient.invokeModel(any(InvokeModelRequest.class))).thenReturn(createInvokeResponse(responseJson));

        BedrockResponse bedrockResponse = BedrockResponse.builder()
                .content(List.of())
                .build();

        when(objectMapper.readValue(eq(responseJson), eq(BedrockResponse.class))).thenReturn(bedrockResponse);

        // Act & Assert
        assertThrows(
                BedrockException.class,
                () -> bedrockService.analyzeIncomeStatement(extractedText)
        );

        verify(jsonResponseParser, never()).parse(anyString());
    }

    @Test
    @DisplayName("Should throw BedrockException when AWS Bedrock invocation throws exception")
    void analyzeIncomeStatement_ShouldThrowBedrockException_WhenAwsInvocationFails() throws Exception {
        // Arrange
        String extractedText = "Revenue";

        when(bedrockPromptBuilder.buildPrompt(extractedText)).thenReturn("Prompt");
        when(bedrockConfig.getModelId()).thenReturn("anthropic.claude-3-haiku-20240307-v1:0");
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");
        when(bedrockRuntimeClient.invokeModel(any(InvokeModelRequest.class)))
                .thenThrow(new RuntimeException("AWS Runtime Failure"));

        // Act & Assert
        assertThrows(
                BedrockException.class,
                () -> bedrockService.analyzeIncomeStatement(extractedText)
        );

        verify(jsonResponseParser, never()).parse(anyString());
    }

    @Test
    @DisplayName("Should throw BedrockException when JsonResponseParser fails")
    void analyzeIncomeStatement_ShouldThrowBedrockException_WhenParserFails() throws Exception {
        // Arrange
        String extractedText = "Revenue";
        String responseJson = "{\"content\":[{\"type\":\"text\",\"text\":\"{}\"}]}";

        when(bedrockPromptBuilder.buildPrompt(extractedText)).thenReturn("Prompt");
        when(bedrockConfig.getModelId()).thenReturn("anthropic.claude-3-haiku-20240307-v1:0");
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");
        when(bedrockRuntimeClient.invokeModel(any(InvokeModelRequest.class))).thenReturn(createInvokeResponse(responseJson));

        BedrockResponse response = createClaudeResponse("{}");
        when(objectMapper.readValue(eq(responseJson), eq(BedrockResponse.class))).thenReturn(response);
        when(jsonResponseParser.parse(anyString()))
                .thenThrow(new BedrockException("Parser Failed"));

        // Act & Assert
        assertThrows(
                BedrockException.class,
                () -> bedrockService.analyzeIncomeStatement(extractedText)
        );
    }

    @Test
    @DisplayName("Should throw BedrockException when ObjectMapper request serialization fails")
    void analyzeIncomeStatement_ShouldThrowBedrockException_WhenObjectMapperSerializationFails() throws Exception {
        // Arrange
        String extractedText = "Revenue";

        when(bedrockPromptBuilder.buildPrompt(extractedText)).thenReturn("Prompt");
        when(objectMapper.writeValueAsString(any()))
                .thenThrow(new RuntimeException("Serialization Failed"));

        // Act & Assert
        assertThrows(
                BedrockException.class,
                () -> bedrockService.analyzeIncomeStatement(extractedText)
        );

        verify(bedrockRuntimeClient, never()).invokeModel(any(InvokeModelRequest.class));
    }
}