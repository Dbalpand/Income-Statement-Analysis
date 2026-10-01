package com.divyesh.incomestatementanalysis.entity;

import com.divyesh.incomestatementanalysis.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "analysis_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalysisHistory extends BaseEntity {

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false)
    private String financialYear;

    @Column(nullable = false)
    private String currency;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String extractedText;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String aiResponse;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String jsonResponse;
}