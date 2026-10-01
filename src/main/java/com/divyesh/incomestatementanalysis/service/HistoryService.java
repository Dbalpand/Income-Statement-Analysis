package com.divyesh.incomestatementanalysis.service;

import com.divyesh.incomestatementanalysis.entity.AnalysisHistory;
import com.divyesh.incomestatementanalysis.model.IncomeStatement;

import java.util.List;

public interface HistoryService {

    /**
     * Persists the record of an income statement analysis.
     *
     * @param fileName Original uploaded file name
     * @param extractedText Raw OCR text output
     * @param jsonResponse Serialized JSON response string
     * @param incomeStatement Domain model with parsed financial data
     * @return Saved AnalysisHistory entity
     */
    AnalysisHistory saveHistory(String fileName, String extractedText, String jsonResponse, IncomeStatement incomeStatement);

    /**
     * Fetches all analysis history records.
     *
     * @return List of AnalysisHistory records
     */
    List<AnalysisHistory> getAllHistory();

    /**
     * Fetches an analysis history record by its ID.
     *
     * @param id History record ID
     * @return AnalysisHistory entity
     */
    AnalysisHistory getHistoryById(Long id);
}