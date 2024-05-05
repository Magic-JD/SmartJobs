package org.smartjobs.core.service;

import org.smartjobs.core.entities.FileInformation;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface FileService {
    Optional<FileInformation> handleFile(MultipartFile file);
}
