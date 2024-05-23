package org.smartjobs.core.service.file.textextractor;

import org.smartjobs.core.exception.categories.AsynchronousExceptions.TextExtractionException;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class TxtTextExtractor implements TextExtractor {

    @Override
    public String extractText(MultipartFile file) {
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append(" ");
            }
        } catch (IOException e) {
            throw new TextExtractionException(file.getOriginalFilename(), e);
        }
        return resultStringBuilder.toString().replaceAll("[^(\\x00-\\xFF)]+(?:$|\\s*)", " ");
    }
}
