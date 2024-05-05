package org.smartjobs.com.core.service.file.textextractor;

import org.springframework.web.multipart.MultipartFile;

public interface TextExtractor {

    String extractText(MultipartFile file);


}
