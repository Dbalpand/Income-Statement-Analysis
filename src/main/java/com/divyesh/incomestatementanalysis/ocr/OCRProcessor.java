package com.divyesh.incomestatementanalysis.ocr;

import com.divyesh.incomestatementanalysis.exception.OCRException;
import com.divyesh.incomestatementanalysis.util.OCRUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OCRProcessor {

    private final Tesseract tesseract;
    private final PdfToImageConverter pdfToImageConverter;
    private final ImagePreprocessor imagePreprocessor;
    private final OCRUtil ocrUtil;

    public OCRResult process(MultipartFile file) {

        long startTime = System.currentTimeMillis();

        try {

            String extractedText;
            int pageCount;

            if (ocrUtil.isPdf(file)) {
                List<BufferedImage> images = pdfToImageConverter.convert(file);
                pageCount = images.size();
                extractedText = processPdf(images);
            } else if (ocrUtil.isImage(file)) {
                BufferedImage image = processImage(file);
                pageCount = 1;
                extractedText = processSingleImage(image);
            } else {
                throw new OCRException("Unsupported file type.");
            }

            extractedText = ocrUtil.cleanText(extractedText);

            return OCRResult.builder()
                    .extractedText(extractedText)
                    .pageCount(pageCount)
                    .processingTime(System.currentTimeMillis() - startTime)
                    .build();

        } catch (Exception ex) {
            log.error("OCR Processing Failed.", ex);
            throw new OCRException("Failed to process OCR document.", ex);
        }
    }

    private String processPdf(List<BufferedImage> images) {

        StringBuilder builder = new StringBuilder();

        int page = 1;

        for (BufferedImage image : images) {

            log.info("Processing PDF Page {}", page);

            BufferedImage processed = imagePreprocessor.preprocess(image);

            builder.append(performOCR(processed));

            builder.append(System.lineSeparator());

            page++;
        }

        return builder.toString();
    }

    private BufferedImage processImage(MultipartFile file) {

        try {

            BufferedImage image = ImageIO.read(file.getInputStream());

            if (image == null) {
                throw new OCRException("Unable to read uploaded image.");
            }

            return imagePreprocessor.preprocess(image);

        } catch (IOException ex) {
            throw new OCRException("Failed to process uploaded image.", ex);
        }
    }

    private String processSingleImage(BufferedImage image) {

        log.info("Processing image using Tesseract.");

        return performOCR(image);
    }

    private String performOCR(BufferedImage image) {

        try {
            return tesseract.doOCR(image);
        } catch (TesseractException ex) {
            throw new OCRException("Failed to perform OCR.", ex);
        }
    }
}
