package org.smartjobs.com.service.file;

import jakarta.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartjobs.com.exception.categories.AsynchronousExceptions.FileTypeNotSupportedException;
import org.smartjobs.com.service.file.data.FileInformation;
import org.smartjobs.com.service.file.data.FileType;
import org.smartjobs.com.service.file.textextractor.PdfTextExtractor;
import org.smartjobs.com.service.file.textextractor.TxtTextExtractor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Optional;

import static org.smartjobs.com.service.file.data.FileType.UNSUPPORTED;

@Service
public class FileService {

    private static final Logger logger = LoggerFactory.getLogger(FileService.class);

    public FileInformation handleFile(MultipartFile file) {
        logger.debug("Handling file {}", file.getOriginalFilename());
        FileType fileType = switch (getFileExtension(file.getOriginalFilename()).orElse("unsupported")) {
            case "pdf" -> FileType.PDF;
            case "txt" -> FileType.TXT;
            default -> UNSUPPORTED;
        };
        String text = extractText(file, fileType);
        logger.debug("File information {} extracted from {}", text, file.getOriginalFilename());
        return new FileInformation(text);
    }

    private String extractText(MultipartFile file, FileType fileType) {
        return switch (fileType) {
            case TXT -> new TxtTextExtractor().extractText(file);
            case PDF -> new PdfTextExtractor().extractText(file);
            case UNSUPPORTED -> throw new FileTypeNotSupportedException(
                    Arrays.stream(FileType.values())
                            .filter(ft -> ft != UNSUPPORTED)
                            .map(ft -> ft.name().toLowerCase()).toList(),
                    file.getOriginalFilename());
        };

    }

    private Optional<String> getFileExtension(@Nullable String fileName) {
        if (fileName == null) {
            return Optional.empty();
        }
        String[] fileNameParts = fileName.split("\\.");

        return Optional.of(fileNameParts[fileNameParts.length - 1]);
    }
}