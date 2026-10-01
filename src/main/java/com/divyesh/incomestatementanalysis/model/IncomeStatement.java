package com.divyesh.incomestatementanalysis.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncomeStatement {

    private String companyName;

    private String financialYear;

    private String currency;

    private List<FinancialItem> items;

}