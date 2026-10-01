package com.divyesh.incomestatementanalysis.service;

import com.divyesh.incomestatementanalysis.ocr.OCRResult;
import org.springframework.web.multipart.MultipartFile;

public interface OCRService {

    /**
     * Extract text from uploaded document.
     *
     * @param file Uploaded PDF/Image
     * @return OCR Result
     */
    OCRResult extractText(MultipartFile file);

}