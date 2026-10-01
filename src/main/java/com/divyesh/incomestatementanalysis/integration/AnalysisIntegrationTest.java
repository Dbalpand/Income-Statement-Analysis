package com.divyesh.incomestatementanalysis.integration;

import com.divyesh.incomestatementanalysis.model.IncomeStatement;
import com.divyesh.incomestatementanalysis.service.AnalysisService;
import com.divyesh.incomestatementanalysis.service.BedrockService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class AnalysisIntegrationTest {

    @Autowired
    private AnalysisService analysisService;

    /**
     * Mock the external AWS Bedrock dependency.
     */
    @MockitoBean
    private BedrockService bedrockService;

    @Test
    void contextLoads() {
        assertNotNull(analysisService);
    }

    @Test
    void analyze_ShouldReturnIncomeStatement() {

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "statement.pdf",
                "application/pdf",
                "Dummy PDF Content".getBytes()
        );

        IncomeStatement incomeStatement = IncomeStatement.builder()
                .companyName("ABC Pvt Ltd")
                .financialYear("2024")
                .currency("INR")
                .build();

        when(bedrockService.analyzeIncomeStatement(any()))
                .thenReturn(incomeStatement);

        // Act - Actually call the service under test using the uploaded file
        IncomeStatement result = analysisService.analyze(file);

        // Assert
        assertNotNull(result);
        assertEquals("ABC Pvt Ltd", result.getCompanyName());
        assertEquals("2024", result.getFinancialYear());
        assertEquals("INR", result.getCurrency());
    }

}