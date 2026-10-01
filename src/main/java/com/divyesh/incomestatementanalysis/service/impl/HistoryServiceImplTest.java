package com.divyesh.incomestatementanalysis.service.impl;

import com.divyesh.incomestatementanalysis.entity.AnalysisHistory;
import com.divyesh.incomestatementanalysis.model.IncomeStatement;
import com.divyesh.incomestatementanalysis.repository.AnalysisHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HistoryServiceImplTest {

    @Mock
    private AnalysisHistoryRepository analysisHistoryRepository;

    @InjectMocks
    private HistoryServiceImpl historyService;

    private IncomeStatement incomeStatement;

    private AnalysisHistory analysisHistory;

    @BeforeEach
    void setUp() {

        incomeStatement = IncomeStatement.builder()
                .companyName("ABC Pvt Ltd")
                .financialYear("2024")
                .currency("INR")
                .build();

        analysisHistory = AnalysisHistory.builder()
                .fileName("income_statement.pdf")
                .companyName("ABC Pvt Ltd")
                .financialYear("2024")
                .currency("INR")
                .extractedText("Revenue : 100000")
                .aiResponse("{\"companyName\":\"ABC Pvt Ltd\"}")
                .jsonResponse("{\"companyName\":\"ABC Pvt Ltd\"}")
                .build();

    }

    @Test
    void saveHistory_ShouldSaveHistorySuccessfully() {

        when(analysisHistoryRepository.save(any(AnalysisHistory.class)))
                .thenReturn(analysisHistory);

        AnalysisHistory result = historyService.saveHistory(
                "income_statement.pdf",
                "Revenue : 100000",
                "{\"companyName\":\"ABC Pvt Ltd\"}",
                incomeStatement
        );

        assertNotNull(result);
        assertEquals("income_statement.pdf", result.getFileName());
        assertEquals("ABC Pvt Ltd", result.getCompanyName());
        assertEquals("2024", result.getFinancialYear());
        assertEquals("INR", result.getCurrency());

        verify(analysisHistoryRepository, times(1))
                .save(any(AnalysisHistory.class));
    }

    @Test
    void getAllHistory_ShouldReturnHistoryList() {

        when(analysisHistoryRepository.findAll())
                .thenReturn(Collections.singletonList(analysisHistory));

        List<AnalysisHistory> historyList =
                historyService.getAllHistory();

        assertNotNull(historyList);
        assertEquals(1, historyList.size());
        assertEquals(
                "ABC Pvt Ltd",
                historyList.get(0).getCompanyName()
        );

        verify(analysisHistoryRepository, times(1))
                .findAll();
    }

    @Test
    void getHistoryById_ShouldReturnHistory_WhenRecordExists() {

        when(analysisHistoryRepository.findById(1L))
                .thenReturn(Optional.of(analysisHistory));

        AnalysisHistory result =
                historyService.getHistoryById(1L);

        assertNotNull(result);
        assertEquals(
                "income_statement.pdf",
                result.getFileName()
        );

        verify(analysisHistoryRepository, times(1))
                .findById(1L);
    }

    @Test
    void getHistoryById_ShouldThrowException_WhenRecordDoesNotExist() {

        when(analysisHistoryRepository.findById(1L))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> historyService.getHistoryById(1L)
                );

        assertEquals(
                "Analysis history not found with id: 1",
                exception.getMessage()
        );

        verify(analysisHistoryRepository, times(1))
                .findById(1L);
    }

}