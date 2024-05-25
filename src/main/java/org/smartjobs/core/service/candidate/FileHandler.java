package org.smartjobs.core.service.candidate;

import org.smartjobs.core.entities.FileInformation;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface FileHandler {
    Optional<FileInformation> handleFile(MultipartFile file);
}
