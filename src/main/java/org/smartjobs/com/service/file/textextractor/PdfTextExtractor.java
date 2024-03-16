package org.smartjobs.com.service.file.textextractor;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class PdfTextExtractor implements TextExtractor {
    @Override
    public String extractText(MultipartFile file) {
        try {
            PDDocument document = Loader.loadPDF(file.getBytes());
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            document.close();
            return text
                    .replaceAll("[^(\\x00-\\xFF)]+(?:$|\\s*)", " ")
                    .replace("\n", " ");
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
