package com.divyesh.incomestatementanalysis.dto.response;

import com.divyesh.incomestatementanalysis.enums.CurrencyType;
import com.divyesh.incomestatementanalysis.model.FinancialItem;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class AnalysisResponse {

    private String companyName;

    private String financialYear;

    private CurrencyType currency;

    private List<FinancialItem> items;

    private String extractedText;

}