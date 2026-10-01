package com.divyesh.incomestatementanalysis.service.impl;

import com.divyesh.incomestatementanalysis.entity.AnalysisHistory;
//import com.divyesh.incomestatementanalysis.exception.ResourceNotFoundException;
import com.divyesh.incomestatementanalysis.model.IncomeStatement;
import com.divyesh.incomestatementanalysis.repository.AnalysisHistoryRepository;
import com.divyesh.incomestatementanalysis.service.HistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HistoryServiceImpl implements HistoryService {

    private final AnalysisHistoryRepository analysisHistoryRepository;

    @Override
    @Transactional
    public AnalysisHistory saveHistory(String fileName, String extractedText, String jsonResponse, IncomeStatement incomeStatement) {
        log.info("Saving analysis history record for file: {}", fileName);

        AnalysisHistory history = AnalysisHistory.builder()
                .fileName(fileName != null ? fileName : "unknown_file")
                .companyName(incomeStatement.getCompanyName() != null ? incomeStatement.getCompanyName() : "N/A")
                .financialYear(incomeStatement.getFinancialYear() != null ? incomeStatement.getFinancialYear() : "N/A")
                .currency(incomeStatement.getCurrency() != null ? incomeStatement.getCurrency() : "N/A")
                .extractedText(extractedText)
                .aiResponse(jsonResponse)
                .jsonResponse(jsonResponse)
                .build();

        return analysisHistoryRepository.save(history);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnalysisHistory> getAllHistory() {
        return analysisHistoryRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public AnalysisHistory getHistoryById(Long id) {
        return analysisHistoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Analysis history not found with id: " + id));
    }
}