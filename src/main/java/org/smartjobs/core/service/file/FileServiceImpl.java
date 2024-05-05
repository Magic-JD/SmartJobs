package org.smartjobs.core.service.file;

import jakarta.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartjobs.core.entities.FileInformation;
import org.smartjobs.core.exception.categories.AsynchronousExceptions.FileTypeNotSupportedException;
import org.smartjobs.core.exception.categories.AsynchronousExceptions.TextExtractionException;
import org.smartjobs.core.service.FileService;
import org.smartjobs.core.service.file.data.FileType;
import org.smartjobs.core.service.file.textextractor.PdfTextExtractor;
import org.smartjobs.core.service.file.textextractor.TxtTextExtractor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Optional;

import static org.smartjobs.core.service.file.data.FileType.*;

@Service
public class FileServiceImpl implements FileService {

    private static final Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    @Override
    public Optional<FileInformation> handleFile(MultipartFile file) {
        logger.debug("Handling file {}", file.getOriginalFilename());
        var hashString = extractHash(file);
        return hashString.flatMap(hash -> {
            logger.debug("File hash {}", hash);
            FileType fileType = switch (getFileExtension(file.getOriginalFilename()).orElse("unsupported")) {
                case "pdf" -> PDF;
                case "txt" -> TXT;
                default -> UNSUPPORTED;
            };
            try {
                String text = extractText(file, fileType);
                logger.debug("File information {} extracted from {}", text, file.getOriginalFilename());
                return Optional.of(new FileInformation(hash, text));
            } catch (TextExtractionException e) {
                logger.error("Failed to resolve the file due to {}", e.getMessage());
                return Optional.empty();
            }
        });

    }

    private Optional<String> extractHash(MultipartFile file) {
        try {
            byte[] uploadBytes = file.getBytes();
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] digest = md5.digest(uploadBytes);
            return Optional.of(new BigInteger(1, digest).toString(16));
        } catch (IOException | NoSuchAlgorithmException e) {
            logger.error("Could not extract hash for file {}", file.getOriginalFilename());
            return Optional.empty();
        }
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