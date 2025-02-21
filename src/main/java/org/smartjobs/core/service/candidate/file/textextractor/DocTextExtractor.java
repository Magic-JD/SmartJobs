package org.smartjobs.core.service.candidate.file.textextractor;


import org.apache.poi.hwpf.extractor.WordExtractor;
import org.smartjobs.core.exception.categories.AsynchronousExceptions.TextExtractionException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class DocTextExtractor implements TextExtractor {
    @Override
    public String extractText(MultipartFile file) {
        try {
            WordExtractor extractor = new WordExtractor(file.getInputStream());
            String text = extractor.getText();
            extractor.close();
            return text;
        } catch (IOException | IllegalArgumentException e) {
            throw new TextExtractionException(file.getOriginalFilename(), e);
        }
    }
}
