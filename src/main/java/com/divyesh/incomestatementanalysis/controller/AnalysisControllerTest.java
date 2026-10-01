package com.divyesh.incomestatementanalysis.controller;

import com.divyesh.incomestatementanalysis.entity.AnalysisHistory;
import com.divyesh.incomestatementanalysis.model.IncomeStatement;
import com.divyesh.incomestatementanalysis.service.AnalysisService;
import com.divyesh.incomestatementanalysis.service.HistoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AnalysisController.class)
@WithMockUser
class AnalysisControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AnalysisService analysisService;

    @MockitoBean
    private HistoryService historyService;

    @Test
    @DisplayName("Upload API should return IncomeStatement")
    void analyzeIncomeStatement_ShouldReturnSuccess() throws Exception {

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "statement.pdf",
                        MediaType.APPLICATION_PDF_VALUE,
                        "dummy pdf".getBytes()
                );

        IncomeStatement incomeStatement =
                IncomeStatement.builder()
                        .companyName("ABC Pvt Ltd")
                        .financialYear("2024")
                        .currency("INR")
                        .build();

        when(analysisService.analyze(any()))
                .thenReturn(incomeStatement);

        mockMvc.perform(
                        multipart("/api/v1/analysis/upload")
                                .file(file)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.companyName")
                        .value("ABC Pvt Ltd"))
                .andExpect(jsonPath("$.data.financialYear")
                        .value("2024"))
                .andExpect(jsonPath("$.data.currency")
                        .value("INR"));

    }

    @Test
    @DisplayName("Upload API should return Bad Request for empty file")
    void analyzeIncomeStatement_ShouldReturnBadRequest() throws Exception {

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "",
                        MediaType.APPLICATION_PDF_VALUE,
                        new byte[0]
                );

        mockMvc.perform(
                        multipart("/api/v1/analysis/upload")
                                .file(file)
                )
                .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("History API should return all records")
    void getAllHistory_ShouldReturnHistoryList() throws Exception {

        AnalysisHistory history =
                AnalysisHistory.builder()
                        .fileName("statement.pdf")
                        .companyName("ABC Pvt Ltd")
                        .financialYear("2024")
                        .currency("INR")
                        .build();

        when(historyService.getAllHistory())
                .thenReturn(Collections.singletonList(history));

        mockMvc.perform(
                        get("/api/v1/analysis/history")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].companyName")
                        .value("ABC Pvt Ltd"));

    }

    @Test
    @DisplayName("History By Id API should return record")
    void getHistoryById_ShouldReturnHistory() throws Exception {

        AnalysisHistory history =
                AnalysisHistory.builder()
                        .fileName("statement.pdf")
                        .companyName("ABC Pvt Ltd")
                        .financialYear("2024")
                        .currency("INR")
                        .build();

        when(historyService.getHistoryById(1L))
                .thenReturn(history);

        mockMvc.perform(
                        get("/api/v1/analysis/history/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.companyName")
                        .value("ABC Pvt Ltd"));

    }

}