package org.smartjobs.com.controller;

import jakarta.validation.Valid;
import org.smartjobs.com.controller.request.AddCandidateRequest;
import org.smartjobs.com.controller.request.EvaluationRequest;
import org.smartjobs.com.controller.response.EvaluationResponse;
import org.smartjobs.com.controller.response.UploadResponse;
import org.smartjobs.com.service.CandidateService;
import org.smartjobs.com.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/candidate")
public class CandidateController {

    @Autowired
    private CandidateService service;

    @Autowired
    private FileService fileService;


    @PostMapping("/evaluate")
    @ResponseBody
    public ResponseEntity<EvaluationResponse> evaluateCandidate(@RequestBody EvaluationRequest request) {
        String evaluation = service.evaluateCv(request.candidateCv());

        return ResponseEntity.ok(new EvaluationResponse(evaluation));
    }

    @PostMapping("/upload")
    public ResponseEntity<UploadResponse> uploadFile(
            @RequestParam(name = "file", required = false) MultipartFile file
    ) {
        FileService.FileInformation fileInformation = fileService.handleFile(file);

        UploadResponse uploadResponse = new UploadResponse("", fileInformation.fileContent());

        return ResponseEntity.ok().body(uploadResponse);
    }

    @PostMapping("/add")
    public HttpStatus addCandidate(@RequestBody @Valid AddCandidateRequest request) {
        service.updateCandidateCv(request.candidateName(), request.cvData());
        return HttpStatus.OK;
    }

}
