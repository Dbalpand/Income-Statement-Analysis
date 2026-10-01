package com.divyesh.incomestatementanalysis.ocr;

import org.springframework.web.multipart.MultipartFile;

public interface OCRService {

    /**
     * Extracts text from an uploaded document or image file.
     *
     * @param file Uploaded PDF or image file
     * @return OCRResult containing extracted text and metadata
     */
    OCRResult extractText(MultipartFile file);

}