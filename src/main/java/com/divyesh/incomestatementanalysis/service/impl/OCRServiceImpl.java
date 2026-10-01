package com.divyesh.incomestatementanalysis.service.impl;

import com.divyesh.incomestatementanalysis.exception.OCRException;
import com.divyesh.incomestatementanalysis.ocr.OCRProcessor;
import com.divyesh.incomestatementanalysis.ocr.OCRResult;
import com.divyesh.incomestatementanalysis.service.OCRService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class OCRServiceImpl implements OCRService {

    private final OCRProcessor ocrProcessor;

    @Override
    public OCRResult extractText(MultipartFile file) {

        try {

            return ocrProcessor.process(file);

        } catch (Exception exception) {

            throw new OCRException(
                    "Unable to process OCR.",
                    exception
            );

        }

    }

}