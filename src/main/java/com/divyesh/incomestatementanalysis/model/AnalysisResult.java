package com.divyesh.incomestatementanalysis.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalysisResult {

    private String extractedText;

    private String aiResponse;

    private IncomeStatement incomeStatement;

}