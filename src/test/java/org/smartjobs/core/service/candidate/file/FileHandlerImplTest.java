package org.smartjobs.core.service.candidate.file;

import org.junit.jupiter.api.Test;
import org.smartjobs.core.entities.FileInformation;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static constants.TestConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class FileHandlerImplTest {

    public static final FileInformation FILE_INFORMATION_TEXT = new FileInformation(HASH_TXT, CV_STRING_FULL);

    @Test
    void testHandleFileReturnsTheCorrectInformationFromATxtFile() {
        Optional<FileInformation> fileInformation = FILE_HANDLER.handleFile(fileTxt());
        assertEquals(Optional.of(FILE_INFORMATION_TEXT), fileInformation);
    }

    @Test
    void testHandleFileInformationReturnsEmptyOnADocFailure() {
        Optional<FileInformation> fileInformation = FILE_HANDLER.handleFile(fileDocFailing());
        assertEquals(Optional.empty(), fileInformation);
    }

    @Test
    void testHandleFileInformationReturnsEmptyOnAPdfFailure() {
        Optional<FileInformation> fileInformation = FILE_HANDLER.handleFile(filePdfFailing());
        assertEquals(Optional.empty(), fileInformation);
    }

    @Test
    void testHandleFileInformationReturnsEmptyOnHashResolutionFailure() throws IOException {
        MultipartFile file = fileTxt();
        file = spy(file);
        doThrow(IOException.class).when(file).getBytes();
        Optional<FileInformation> fileInformation = FILE_HANDLER.handleFile(file);
        assertEquals(Optional.empty(), fileInformation);
    }

    @Test
    void testHandleFileInformationReturnsEmptyOnNullName() {
        MultipartFile file = fileTxt();
        file = spy(file);
        doReturn(null).when(file).getOriginalFilename();
        Optional<FileInformation> fileInformation = FILE_HANDLER.handleFile(file);
        assertEquals(Optional.empty(), fileInformation);
    }
}