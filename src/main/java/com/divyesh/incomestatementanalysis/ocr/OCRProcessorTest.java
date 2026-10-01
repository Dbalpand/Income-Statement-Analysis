package com.divyesh.incomestatementanalysis.ocr;

import com.divyesh.incomestatementanalysis.exception.OCRException;
import com.divyesh.incomestatementanalysis.util.OCRUtil;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OCRProcessorTest {

    @Mock
    private Tesseract tesseract;

    @Mock
    private PdfToImageConverter pdfToImageConverter;

    @Mock
    private ImagePreprocessor imagePreprocessor;

    @Mock
    private OCRUtil ocrUtil;

    @InjectMocks
    private OCRProcessor ocrProcessor;

    private BufferedImage image;

    @BeforeEach
    void setUp() {

        image = new BufferedImage(
                200,
                200,
                BufferedImage.TYPE_INT_RGB
        );

    }

    @Test
    void process_ShouldProcessPdfSuccessfully() throws Exception {

        MultipartFile pdf =
                new MockMultipartFile(
                        "file",
                        "statement.pdf",
                        "application/pdf",
                        "dummy".getBytes()
                );

        when(ocrUtil.isPdf(pdf)).thenReturn(true);

        when(pdfToImageConverter.convert(pdf))
                .thenReturn(List.of(image));

        when(imagePreprocessor.preprocess(any()))
                .thenReturn(image);

        when(tesseract.doOCR(any(BufferedImage.class)))
                .thenReturn("Revenue 100000");

        when(ocrUtil.cleanText(anyString()))
                .thenReturn("Revenue 100000");

        OCRResult result = ocrProcessor.process(pdf);

        assertNotNull(result);
        assertEquals(
                "Revenue 100000",
                result.getExtractedText()
        );

        assertEquals(
                1,
                result.getPageCount()
        );

        verify(pdfToImageConverter).convert(pdf);

    }

    @Test
    void process_ShouldThrowOCRException_WhenUnsupportedFile() {

        MultipartFile file =
                new MockMultipartFile(
                        "file",
                        "test.txt",
                        "text/plain",
                        "hello".getBytes()
                );

        when(ocrUtil.isPdf(file))
                .thenReturn(false);

        when(ocrUtil.isImage(file))
                .thenReturn(false);

        assertThrows(
                OCRException.class,
                () -> ocrProcessor.process(file)
        );

    }

    @Test
    void process_ShouldThrowOCRException_WhenPdfConversionFails() {

        MultipartFile pdf =
                new MockMultipartFile(
                        "file",
                        "statement.pdf",
                        "application/pdf",
                        "dummy".getBytes()
                );

        when(ocrUtil.isPdf(pdf))
                .thenReturn(true);

        when(pdfToImageConverter.convert(pdf))
                .thenThrow(new OCRException("PDF conversion failed"));

        assertThrows(
                OCRException.class,
                () -> ocrProcessor.process(pdf)
        );

    }

    @Test
    void process_ShouldThrowOCRException_WhenTesseractFails() throws Exception {

        MultipartFile pdf =
                new MockMultipartFile(
                        "file",
                        "statement.pdf",
                        "application/pdf",
                        "dummy".getBytes()
                );

        when(ocrUtil.isPdf(pdf))
                .thenReturn(true);

        when(pdfToImageConverter.convert(pdf))
                .thenReturn(List.of(image));

        when(imagePreprocessor.preprocess(any()))
                .thenReturn(image);

        when(tesseract.doOCR(any(BufferedImage.class)))
                .thenThrow(new TesseractException("OCR Failed"));

        assertThrows(
                OCRException.class,
                () -> ocrProcessor.process(pdf)
        );

    }

    @Test
    void process_ShouldCleanExtractedText() throws Exception {

        MultipartFile pdf =
                new MockMultipartFile(
                        "file",
                        "statement.pdf",
                        "application/pdf",
                        "dummy".getBytes()
                );

        when(ocrUtil.isPdf(pdf))
                .thenReturn(true);

        when(pdfToImageConverter.convert(pdf))
                .thenReturn(List.of(image));

        when(imagePreprocessor.preprocess(any()))
                .thenReturn(image);

        // ✅ Explicit type matching
        when(tesseract.doOCR(any(BufferedImage.class))).thenReturn(" Revenue    100000 ");

        when(ocrUtil.cleanText(anyString()))
                .thenReturn("Revenue 100000");

        OCRResult result =
                ocrProcessor.process(pdf);

        assertEquals(
                "Revenue 100000",
                result.getExtractedText()
        );

        verify(ocrUtil).cleanText(anyString());

    }

}