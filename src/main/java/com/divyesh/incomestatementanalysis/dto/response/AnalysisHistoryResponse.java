package com.divyesh.incomestatementanalysis.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class AnalysisHistoryResponse {

    private Long id;

    private String fileName;

    private String companyName;

    private String financialYear;

    private String currency;

    private LocalDateTime createdAt;

}