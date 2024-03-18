package org.smartjobs.com.controller.candidate;

import org.smartjobs.com.service.candidate.CandidateService;
import org.smartjobs.com.service.file.FileService;
import org.smartjobs.com.service.file.data.FileInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class CandidateController {

    private final CandidateService candidateService;
    private final FileService fileService;

    @Autowired
    public CandidateController(CandidateService candidateService, FileService fileService) {
        this.candidateService = candidateService;
        this.fileService = fileService;
    }

    @PostMapping("/upload")
    public HttpStatus uploadFile(
            @RequestParam(name = "files") MultipartFile[] files
    ) {
        Arrays.stream(files).parallel().map(fileService::handleFile).forEach(candidateService::updateCandidateCv);
        return HttpStatus.OK;
    }
}
