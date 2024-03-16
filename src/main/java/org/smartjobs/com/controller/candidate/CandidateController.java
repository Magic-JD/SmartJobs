package org.smartjobs.com.controller.candidate;

import org.smartjobs.com.service.candidate.CandidateService;
import org.smartjobs.com.service.file.FileService;
import org.smartjobs.com.service.file.data.FileInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/candidate")
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
            @RequestParam(name = "file", required = false) MultipartFile file
    ) {
        FileInformation fileInformation = fileService.handleFile(file);
        candidateService.updateCandidateCv(fileInformation);

        return HttpStatus.OK;
    }
}
