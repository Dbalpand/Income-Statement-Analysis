package com.divyesh.incomestatementanalysis.service.impl;

import com.divyesh.incomestatementanalysis.model.IncomeStatement;
import com.divyesh.incomestatementanalysis.ocr.OCRResult;
import com.divyesh.incomestatementanalysis.ocr.OCRService;
import com.divyesh.incomestatementanalysis.service.AnalysisService;
import com.divyesh.incomestatementanalysis.service.BedrockService;
import com.divyesh.incomestatementanalysis.service.HistoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalysisServiceImpl implements AnalysisService {

    private final OCRService ocrService;
    private final BedrockService bedrockService;
    private final HistoryService historyService;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public IncomeStatement analyze(MultipartFile file) {
        log.info("Starting income statement analysis workflow for file: {}", file.getOriginalFilename());

        // 1. Execute OCR to extract text from the uploaded document
        OCRResult ocrResult = ocrService.extractText(file);
        String extractedText = ocrResult.getExtractedText();

        // 2. Invoke AWS Bedrock to analyze the text and parse structured data
        IncomeStatement incomeStatement = bedrockService.analyzeIncomeStatement(extractedText);

        // 3. Serialize parsed IncomeStatement to JSON for historical audit storage
        String jsonResponse;
        try {
            jsonResponse = objectMapper.writeValueAsString(incomeStatement);
        } catch (Exception e) {
            log.warn("Failed to serialize IncomeStatement to JSON for history logging", e);
            jsonResponse = "{}";
        }

        // 4. Save analysis history asynchronously or within the active transaction
        historyService.saveHistory(
                file.getOriginalFilename(),
                extractedText,
                jsonResponse,
                incomeStatement
        );

        log.info("Income statement analysis workflow completed successfully for: {}", file.getOriginalFilename());

        return incomeStatement;
    }
}