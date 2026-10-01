package com.divyesh.incomestatementanalysis.ocr;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OCRResult {

    private String extractedText;

    private int pageCount;

    private long processingTime;

}