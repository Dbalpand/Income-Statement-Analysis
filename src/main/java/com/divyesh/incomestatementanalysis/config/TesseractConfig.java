package com.divyesh.incomestatementanalysis.config;

import net.sourceforge.tess4j.Tesseract;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TesseractConfig {

    @Value("${ocr.tesseract.path}")
    private String executablePath;

    @Value("${ocr.language}")
    private String language;

    @Bean
    public Tesseract tesseract() {

        Tesseract tesseract = new Tesseract();

        String tessData = executablePath
                .replace("tesseract.exe", "")
                + "tessdata";

        tesseract.setDatapath(tessData);

        tesseract.setLanguage(language);

        return tesseract;

    }

}