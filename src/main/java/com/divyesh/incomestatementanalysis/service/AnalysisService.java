package com.divyesh.incomestatementanalysis.service;

import com.divyesh.incomestatementanalysis.model.IncomeStatement;
import org.springframework.web.multipart.MultipartFile;

public interface AnalysisService {

    /**
     * Analyze the uploaded income statement.
     *
     * Flow:
     * Upload File
     *      ↓
     * OCR
     *      ↓
     * AWS Bedrock
     *      ↓
     * JSON Parser
     *      ↓
     * Save History
     *      ↓
     * Return IncomeStatement
     *
     * @param file Uploaded PDF/Image
     * @return Parsed IncomeStatement
     */
    IncomeStatement analyze(MultipartFile file);

}