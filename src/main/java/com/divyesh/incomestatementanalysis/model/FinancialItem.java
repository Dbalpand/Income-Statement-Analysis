package com.divyesh.incomestatementanalysis.model;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinancialItem {

    private String name;

    private BigDecimal amount;

    private Double confidence;

    private String source;
}