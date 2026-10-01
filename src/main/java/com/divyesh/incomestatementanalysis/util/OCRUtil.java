package com.divyesh.incomestatementanalysis.util;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Component
public class OCRUtil {

    private static final Set<String> IMAGE_TYPES = Set.of(
            "image/png",
            "image/jpeg",
            "image/jpg"
    );

    private static final String PDF_TYPE = "application/pdf";

    /**
     * Check whether the uploaded file is a PDF.
     */
    public boolean isPdf(MultipartFile file) {

        return file != null
                && PDF_TYPE.equalsIgnoreCase(file.getContentType());

    }

    /**
     * Check whether the uploaded file is an image.
     */
    public boolean isImage(MultipartFile file) {

        return file != null
                && IMAGE_TYPES.contains(file.getContentType());

    }

    /**
     * Get file extension.
     */
    public String getFileExtension(String fileName) {

        if (fileName == null || !fileName.contains(".")) {
            return "";
        }

        return fileName.substring(fileName.lastIndexOf('.') + 1)
                .toLowerCase();

    }

    /**
     * Normalize OCR text.
     */
    public String cleanText(String text) {

        if (text == null) {
            return "";
        }

        return text
                .replaceAll("\\r", "")
                .replaceAll("\\t", " ")
                .replaceAll(" +", " ")
                .replaceAll("\\n{2,}", "\n")
                .trim();

    }

    /**
     * Check supported file.
     */
    public boolean isSupported(MultipartFile file) {

        return isPdf(file) || isImage(file);

    }

}