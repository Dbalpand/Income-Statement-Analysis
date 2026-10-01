package com.divyesh.incomestatementanalysis.ocr;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.image.BufferedImage;

@Slf4j
@Component
public class ImagePreprocessor {

    /**
     * Preprocess image before OCR.
     *
     * Steps:
     * 1. Convert to Grayscale
     * 2. Apply Binary Threshold
     *
     * @param originalImage Original image
     * @return Processed image
     */
    public BufferedImage preprocess(BufferedImage originalImage) {

        log.info("Starting image preprocessing...");

        BufferedImage grayImage = convertToGrayScale(originalImage);

        BufferedImage binaryImage = applyThreshold(grayImage);

        log.info("Image preprocessing completed.");

        return binaryImage;
    }

    /**
     * Convert RGB image to grayscale.
     */
    private BufferedImage convertToGrayScale(BufferedImage image) {

        BufferedImage grayImage = new BufferedImage(
                image.getWidth(),
                image.getHeight(),
                BufferedImage.TYPE_BYTE_GRAY
        );

        Graphics2D graphics = grayImage.createGraphics();

        graphics.drawImage(image, 0, 0, null);

        graphics.dispose();

        return grayImage;
    }

    /**
     * Convert grayscale image to black & white.
     */
    private BufferedImage applyThreshold(BufferedImage image) {

        BufferedImage binaryImage = new BufferedImage(
                image.getWidth(),
                image.getHeight(),
                BufferedImage.TYPE_BYTE_BINARY
        );

        Graphics2D graphics = binaryImage.createGraphics();

        graphics.drawImage(image, 0, 0, null);

        graphics.dispose();

        return binaryImage;
    }

}