package com.divyesh.incomestatementanalysis.service;

import com.divyesh.incomestatementanalysis.model.IncomeStatement;

/**
 * Service responsible for interacting with AWS Bedrock.
 */
public interface BedrockService {

    /**
     * Sends OCR extracted text to AWS Bedrock (Anthropic Claude Haiku)
     * and converts the response into an IncomeStatement object.
     *
     * @param extractedText OCR extracted text
     * @return Parsed IncomeStatement
     */
    IncomeStatement analyzeIncomeStatement(String extractedText);

}