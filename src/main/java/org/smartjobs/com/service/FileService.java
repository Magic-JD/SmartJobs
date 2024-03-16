package org.smartjobs.com.service;

import jakarta.annotation.Nullable;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.Optional;

@Service
public class FileService {

    private final Path fileStorageLocation;

    @Autowired
    public FileService(Environment env) {
        this.fileStorageLocation = Paths.get(env.getProperty("app.file.upload-dir", "./uploads/files"))
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException(
                    "Could not create the directory where the uploaded files will be stored.", ex);
        }
    }


    public String extractText(MultipartFile file, FileType fileType) {
        return switch (fileType) {
            case TXT -> {
                StringBuilder resultStringBuilder = new StringBuilder();
                try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        resultStringBuilder.append(line).append(" ");
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                yield resultStringBuilder.toString().replaceAll("[^(\\x00-\\xFF)]+(?:$|\\s*)", "");
            }
            case PDF -> {
                try {
                    PDDocument document = Loader.loadPDF(file.getBytes());
                    PDFTextStripper stripper = new PDFTextStripper();
                    String text = stripper.getText(document);
                    document.close();
                    yield text
                            .replaceAll("[^(\\x00-\\xFF)]+(?:$|\\s*)", "")
                            .replace("\n", "");
                } catch (IOException e) {
                    throw new RuntimeException();
                }
            }
            case UNSUPPORTED -> throw new UnsupportedOperationException();
        };

    }

    private Optional<String> getFileExtension(@Nullable String fileName) {
        if (fileName == null) {
            return Optional.empty();
        }
        String[] fileNameParts = fileName.split("\\.");

        return Optional.of(fileNameParts[fileNameParts.length - 1]);
    }

    public String storeFile(MultipartFile file) {
        // Normalize file name
        String fileName = getFileExtension(file.getOriginalFilename()).map(fn -> new Date().getTime() + "-file." + fn).orElseThrow();

        try {
            // Check if the filename contains invalid characters
            if (fileName.contains("..")) {
                throw new RuntimeException(
                        "Sorry! Filename contains invalid path sequence " + fileName);
            }

            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    public FileInformation handleFile(MultipartFile file) {
        FileType fileType = switch (getFileExtension(file.getOriginalFilename()).orElse("unsupported")) {
            case "pdf" -> FileType.PDF;
            case "txt" -> FileType.TXT;
            default -> FileType.UNSUPPORTED;
        };
        String text = extractText(file, fileType);
        return new FileInformation("PATH", text);
    }

    public record FileInformation(String filePath, String fileContent) {
    }

    private enum FileType {
        TXT,
        PDF,
        UNSUPPORTED
    }
}