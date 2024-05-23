package org.smartjobs.core.service.file.textextractor;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.smartjobs.core.exception.categories.AsynchronousExceptions.TextExtractionException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.stream.Collectors;

public class DocxTextExtractor implements TextExtractor {
    @Override
    public String extractText(MultipartFile file) {
        try {
            var document = new XWPFDocument(file.getInputStream());
            var paragraphs = document.getParagraphs().stream()
                    .map(XWPFParagraph::getText)
                    .collect(Collectors.joining(" "));
            document.close();
            return paragraphs;
        } catch (IOException e) {
            throw new TextExtractionException(file.getOriginalFilename(), e);
        }
    }
}
