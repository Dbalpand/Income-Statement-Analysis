package com.divyesh.incomestatementanalysis.service.impl;

import com.divyesh.incomestatementanalysis.exception.BedrockException;
import com.divyesh.incomestatementanalysis.model.IncomeStatement;
import com.divyesh.incomestatementanalysis.ocr.OCRResult;
import com.divyesh.incomestatementanalysis.ocr.OCRService;
import com.divyesh.incomestatementanalysis.service.BedrockService;
import com.divyesh.incomestatementanalysis.service.HistoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalysisServiceImplTest {

    @Mock
    private OCRService ocrService;

    @Mock
    private BedrockService bedrockService;

    @Mock
    private HistoryService historyService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private AnalysisServiceImpl analysisService;

    private MultipartFile multipartFile;
    private OCRResult ocrResult;
    private IncomeStatement incomeStatement;

    @BeforeEach
    void setUp() {

        multipartFile = new MockMultipartFile(
                "file",
                "income_statement.pdf",
                "application/pdf",
                "Sample PDF".getBytes()
        );

        ocrResult = OCRResult.builder()
                .extractedText("Revenue : 100000")
                .pageCount(1)
                .processingTime(500)
                .build();

        incomeStatement = IncomeStatement.builder()
                .companyName("ABC Pvt Ltd")
                .financialYear("2024")
                .currency("INR")
                .build();
    }

    @Test
    void analyze_ShouldReturnIncomeStatement_WhenEverythingSucceeds() throws Exception {

        when(ocrService.extractText(any(MultipartFile.class)))
                .thenReturn(ocrResult);

        when(bedrockService.analyzeIncomeStatement(anyString()))
                .thenReturn(incomeStatement);

        when(objectMapper.writeValueAsString(any()))
                .thenReturn("{\"companyName\":\"ABC Pvt Ltd\"}");

        IncomeStatement result =
                analysisService.analyze(multipartFile);

        assertNotNull(result);
        assertEquals("ABC Pvt Ltd", result.getCompanyName());
        assertEquals("2024", result.getFinancialYear());
        assertEquals("INR", result.getCurrency());

        verify(ocrService, times(1))
                .extractText(any(MultipartFile.class));

        verify(bedrockService, times(1))
                .analyzeIncomeStatement(anyString());

        verify(historyService, times(1))
                .saveHistory(
                        anyString(),
                        anyString(),
                        anyString(),
                        any(IncomeStatement.class)
                );
    }

    @Test
    void analyze_ShouldThrowException_WhenOCRFails() {

        when(ocrService.extractText(any(MultipartFile.class)))
                .thenThrow(new RuntimeException("OCR Failed"));

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> analysisService.analyze(multipartFile)
                );

        assertEquals(
                "OCR Failed",
                exception.getMessage()
        );

        verify(bedrockService, never())
                .analyzeIncomeStatement(anyString());

        verify(historyService, never())
                .saveHistory(
                        anyString(),
                        anyString(),
                        anyString(),
                        any()
                );
    }

    @Test
    void analyze_ShouldThrowException_WhenBedrockFails() {

        when(ocrService.extractText(any(MultipartFile.class)))
                .thenReturn(ocrResult);

        when(bedrockService.analyzeIncomeStatement(anyString()))
                .thenThrow(new BedrockException("Bedrock Failed"));

        BedrockException exception =
                assertThrows(
                        BedrockException.class,
                        () -> analysisService.analyze(multipartFile)
                );

        assertEquals(
                "Bedrock Failed",
                exception.getMessage()
        );

        verify(historyService, never())
                .saveHistory(
                        anyString(),
                        anyString(),
                        anyString(),
                        any()
                );
    }

    @Test
    void analyze_ShouldContinue_WhenJsonSerializationFails() throws Exception {

        when(ocrService.extractText(any(MultipartFile.class)))
                .thenReturn(ocrResult);

        when(bedrockService.analyzeIncomeStatement(anyString()))
                .thenReturn(incomeStatement);

        when(objectMapper.writeValueAsString(any()))
                .thenThrow(new RuntimeException("Serialization Error"));

        IncomeStatement result =
                analysisService.analyze(multipartFile);

        assertNotNull(result);

        verify(historyService, times(1))
                .saveHistory(
                        anyString(),
                        anyString(),
                        eq("{}"),
                        any(IncomeStatement.class)
                );
    }

}