package com.divyesh.incomestatementanalysis.ocr;

import com.divyesh.incomestatementanalysis.exception.OCRException;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class PdfToImageConverter {

    /**
     * Convert every page of a PDF into BufferedImage.
     *
     * @param pdfFile uploaded PDF
     * @return list of page images
     */
    public List<BufferedImage> convert(MultipartFile pdfFile) {

        List<BufferedImage> images = new ArrayList<>();

        try (PDDocument document = Loader.loadPDF(pdfFile.getBytes())) {

            PDFRenderer renderer = new PDFRenderer(document);

            int totalPages = document.getNumberOfPages();

            log.info("Total PDF Pages: {}", totalPages);

            for (int page = 0; page < totalPages; page++) {

                BufferedImage image = renderer.renderImageWithDPI(
                        page,
                        300,
                        ImageType.RGB
                );

                images.add(image);

                log.info("Converted Page {} successfully.", page + 1);
            }

            return images;

        } catch (IOException ex) {

            log.error("PDF conversion failed.", ex);

            throw new OCRException(
                    "Unable to convert PDF into images.",
                    ex
            );

        }

    }

}