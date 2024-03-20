package org.smartjobs.com.controller.files;

import org.smartjobs.com.service.file.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Controller
public class FileController {

    @Autowired
    private FileService fileService;

    @GetMapping("/files/{topScorerCv}")
    public ResponseEntity<byte[]> getFile(@PathVariable String id) {
        File fileDB = fileService.getFile(id);

        try {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, STR. "attachment; filename=\{ fileDB.getName() }" )
                    .body(Files.readAllBytes(fileDB.toPath()));
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
