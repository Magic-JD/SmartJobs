package org.smartjobs.core.service.candidate.file.textextractor;

import org.springframework.web.multipart.MultipartFile;

public interface TextExtractor {

    String extractText(MultipartFile file);


}
