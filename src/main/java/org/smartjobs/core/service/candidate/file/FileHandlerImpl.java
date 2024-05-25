package org.smartjobs.core.service.candidate.file;

import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.smartjobs.core.entities.FileInformation;
import org.smartjobs.core.exception.categories.AsynchronousExceptions.FileTypeNotSupportedException;
import org.smartjobs.core.exception.categories.AsynchronousExceptions.TextExtractionException;
import org.smartjobs.core.service.candidate.FileHandler;
import org.smartjobs.core.service.candidate.file.textextractor.DocTextExtractor;
import org.smartjobs.core.service.candidate.file.textextractor.DocxTextExtractor;
import org.smartjobs.core.service.candidate.file.textextractor.PdfTextExtractor;
import org.smartjobs.core.service.candidate.file.textextractor.TxtTextExtractor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@Component
@Slf4j
public class FileHandlerImpl implements FileHandler {


    @Override
    public Optional<FileInformation> handleFile(MultipartFile file) {
        log.debug("Handling file {}", file.getOriginalFilename());
        var hashString = extractHash(file);
        return hashString.flatMap(hash -> {
            log.debug("File hash {}", hash);
            try {
                String text = switch (getFileExtension(file.getOriginalFilename()).orElse("unsupported")) {
                    case "pdf" -> new PdfTextExtractor().extractText(file);
                    case "txt" -> new TxtTextExtractor().extractText(file);
                    case "doc" -> new DocTextExtractor().extractText(file);
                    case "docx" -> new DocxTextExtractor().extractText(file);
                    default -> throw new FileTypeNotSupportedException(file.getOriginalFilename());
                };

                log.debug("File information {} extracted from {}", text, file.getOriginalFilename());
                return Optional.of(new FileInformation(hash, text));
            } catch (TextExtractionException e) {
                log.error("Failed to resolve the file due to {}", e.getMessage());
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
            log.error("Could not extract hash for file {}", file.getOriginalFilename());
            return Optional.empty();
        }
    }

    private Optional<String> getFileExtension(@Nullable String fileName) {
        if (fileName == null) {
            return Optional.empty();
        }
        String[] fileNameParts = fileName.split("\\.");

        return Optional.of(fileNameParts[fileNameParts.length - 1]);
    }
}