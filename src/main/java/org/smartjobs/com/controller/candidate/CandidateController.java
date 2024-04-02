package org.smartjobs.com.controller.candidate;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.websocket.server.PathParam;
import org.smartjobs.com.dal.repository.data.Cv;
import org.smartjobs.com.exception.categories.UserResolvedExceptions.NotEnoughCreditException;
import org.smartjobs.com.service.auth.AuthService;
import org.smartjobs.com.service.candidate.CandidateService;
import org.smartjobs.com.service.candidate.data.CandidateData;
import org.smartjobs.com.service.chroma.ChromaService;
import org.smartjobs.com.service.credit.CreditService;
import org.smartjobs.com.service.file.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.smartjobs.com.constants.ThymeleafConstants.*;

@Controller
@RequestMapping("/candidate")
public class CandidateController {


    private final CandidateService candidateService;
    private final FileService fileService;
    private final CreditService creditService;
    private final AuthService authService;
    private final ChromaService chromaService;


    @Autowired
    public CandidateController(FileService fileService, CandidateService candidateService, CreditService creditService, AuthService authService, ChromaService chromaService) {
        this.fileService = fileService;
        this.candidateService = candidateService;
        this.creditService = creditService;
        this.authService = authService;
        this.chromaService = chromaService;
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam(name = "files") MultipartFile[] files, HttpServletResponse response) {
        String currentUsername = authService.getCurrentUsername();
        if (!creditService.userHasEnoughCredits(currentUsername)) {
            throw new NotEnoughCreditException();
        }
        var handledFiles = Arrays.stream(files)
                .map(fileService::handleFile)
                .toList();
        List<Cv> cvs = candidateService.updateCandidateCvs(currentUsername, handledFiles);
        cvs.forEach(cv -> chromaService.addCv(cv.getId(), cv.getCondensedText()));
        response.addHeader("HX-Redirect", "/candidates");
        return EMPTY_FRAGMENT;
    }


    @GetMapping()
    public String getAllCandidates(Model model) {
        String currentUsername = authService.getCurrentUsername();
        var candidates = candidateService.getCurrentCandidates(currentUsername).stream()
                .sorted(Comparator.comparing(CandidateData::name)).toList();
        model.addAttribute("candidates", candidates);
        return CANDIDATE_TABLE_FRAGMENT;
    }

    @DeleteMapping("/delete/{cvId}")
    public String deleteCandidate(@PathVariable long cvId) {
        String currentUsername = authService.getCurrentUsername();
        candidateService.deleteCandidate(currentUsername, cvId);
        return EMPTY_FRAGMENT;
    }


    @PutMapping("/select/{cvId}")
    public String deleteCandidate(@PathVariable long cvId, @PathParam("select") boolean select, Model model) {
        String currentUsername = authService.getCurrentUsername();
        return candidateService.toggleCandidateSelect(currentUsername, cvId, select).map(cvData -> {
            model.addAttribute("candidate", cvData);
            return SINGLE_CANDIDATE_ROW_FRAGMENT;
        }).orElse(EMPTY_FRAGMENT);

    }

    @GetMapping("/number/selected")
    public String findNumberOfCandidatesSelected(Model model) {
        int selectedCount = candidateService.findSelectedCandidateCount();
        model.addAttribute("selectedCount", selectedCount);
        return CANDIDATE_COUNT_FRAGMENT;
    }

}
