package org.smartjobs.com.controller.candidate;

import jakarta.servlet.http.HttpServletResponse;
import org.smartjobs.com.exception.categories.UserResolvedExceptions.NotEnoughCreditException;
import org.smartjobs.com.service.auth.AuthService;
import org.smartjobs.com.service.candidate.CandidateService;
import org.smartjobs.com.service.credit.CreditService;
import org.smartjobs.com.service.file.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

@Controller
@RequestMapping("/candidate")
public class CandidateController {

    private final CandidateService candidateService;
    private final FileService fileService;
    private final CreditService creditService;
    private final AuthService authService;



    @Autowired
    public CandidateController(FileService fileService, CandidateService candidateService, CreditService creditService, AuthService authService) {
        this.fileService = fileService;
        this.candidateService = candidateService;
        this.creditService = creditService;
        this.authService = authService;
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam(name = "files") MultipartFile[] files, HttpServletResponse response) {
        String currentUsername = authService.getCurrentUsername();
        if (!creditService.userHasEnoughCredits(currentUsername)) {
            throw new NotEnoughCreditException();
        }
        var handledFiles = Arrays.stream(files).parallel().map(fileService::handleFile);
        candidateService.updateCandidateCvs(currentUsername, handledFiles);
        response.addHeader("HX-Redirect", "/candidates");
        return "";
    }


    @GetMapping()
    public String getAllCandidates(Model model) {
        String currentUsername = authService.getCurrentUsername();
        var candidates = candidateService.getCurrentCandidates(currentUsername);
        model.addAttribute("candidates", candidates);
        return "table";
    }

    @DeleteMapping("/delete/{filePath}")
    @ResponseBody
    public String deleteCandidate(@PathVariable String filePath) {
        String currentUsername = authService.getCurrentUsername();
        candidateService.deleteCandidate(currentUsername, filePath);
        return "";
    }


}
