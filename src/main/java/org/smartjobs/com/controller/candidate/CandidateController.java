package org.smartjobs.com.controller.candidate;

import org.smartjobs.com.service.candidate.CandidateService;
import org.smartjobs.com.service.file.FileService;
import org.smartjobs.com.service.file.data.FileInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/candidate")
public class CandidateController {

    private final CandidateService candidateService;
    private final FileService fileService;

    @Autowired
    public CandidateController(FileService fileService, CandidateService candidateService) {
        this.fileService = fileService;
        this.candidateService = candidateService;
    }

    @PostMapping("/upload")
    public String uploadFile(Model model, @RequestParam(name = "files") MultipartFile[] files) {
        List<FileInformation> handledFiles = Arrays.stream(files).map(fileService::handleFile).toList();
        candidateService.updateCandidateCvs(handledFiles);
        model.addAttribute("candidates", candidateService.getCurrentCandidates());
        return "table";
    }

    @PostMapping("/match")
    public String evaluateCandidate(@RequestParam String listing, Model model) {
        return candidateService.analyseListing(listing).map(listingAnalysis -> {
            model.addAttribute("gptJustification", listingAnalysis.gptJustification());
            model.addAttribute("numberTop", listingAnalysis.numberTop());
            model.addAttribute("topScorerName", listingAnalysis.topScorerName());
            model.addAttribute("topScorers", listingAnalysis.topScorers());
            model.addAttribute("topScorerCv", listingAnalysis.topScorerCv());
            return "gptchat";
        }).orElse("emptychat");


    }
}
