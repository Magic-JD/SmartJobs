package org.smartjobs.com.controller.candidate;

import org.smartjobs.com.service.candidate.CandidateService;
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

    @Autowired
    public CandidateController(FileService fileService, CandidateService candidateService) {
        this.fileService = fileService;
        this.candidateService = candidateService;
    }

    @PostMapping("/upload")
    public String uploadFile(Model model, @RequestParam(name = "files") MultipartFile[] files) {
        var handledFiles = Arrays.stream(files).parallel().map(fileService::handleFile);
        candidateService.updateCandidateCvs(handledFiles);
        model.addAttribute("candidates", candidateService.getCurrentCandidates());
        return "table";
    }

    @GetMapping()
    public String getAllCandidates(Model model) {
        var candidates = candidateService.getCurrentCandidates();
        model.addAttribute("candidates", candidates);
        return "table";
    }

    @DeleteMapping("/delete/{filePath}")
    @ResponseBody
    public void deleteCandidate(@PathVariable String filePath) {
        fileService.deleteFile(filePath);
        candidateService.deleteCandidate(filePath);
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
