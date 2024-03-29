package org.smartjobs.com.controller.analysis;

import org.smartjobs.com.client.gpt.GptClient;
import org.smartjobs.com.service.analysis.AnalysisService;
import org.smartjobs.com.service.auth.AuthService;
import org.smartjobs.com.service.candidate.CandidateService;
import org.smartjobs.com.service.candidate.data.ProcessedCv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@RequestMapping("/analysis")
public class AnalysisController {

    private final CandidateService candidateService;
    private final AnalysisService analysisService;
    private final AuthService authService;

    private final ConcurrentHashMap<String, GptClient.ScoringCriteriaResult> cache = new ConcurrentHashMap<>();

    @Autowired
    public AnalysisController(CandidateService candidateService, AnalysisService analysisService, AuthService authService) {
        this.candidateService = candidateService;
        this.analysisService = analysisService;
        this.authService = authService;
    }


    @PostMapping("/match")
    public String evaluateCandidate(@RequestParam String listing, Model model) {
        String username = authService.getCurrentUsername();
        List<ProcessedCv> candidateInformation = candidateService.getFullCandidateInfo(username);
        return analysisService.analyseListing(listing, candidateInformation).map(listingAnalysis -> {
            model.addAttribute("gptJustification", listingAnalysis.gptJustification());
            model.addAttribute("numberTop", listingAnalysis.numberTop());
            model.addAttribute("topScorerName", listingAnalysis.topScorerName());
            model.addAttribute("topScorers", listingAnalysis.topScorers());
            model.addAttribute("topScorerCv", listingAnalysis.topScorerCv());
            return "top-scorers";
        }).orElse("emptychat");
    }

    @GetMapping("/scoring")
    public String scoreAllCandidates(Model model) {
        String username = authService.getCurrentUsername();
        List<ProcessedCv> candidateInformation = candidateService.getFullCandidateInfo(username);
        var results = analysisService.scoreToCriteria(candidateInformation);
        results.forEach(result -> cache.put(result.uuid(), result));
        model.addAttribute("results", results);
        return "scoring";
    }

    @GetMapping("/result/details/{resultUuid}")
    public String retrieveResultDetails(@PathVariable String resultUuid, Model model) {
        model.addAttribute("result", cache.get(resultUuid));
        return "result-details";
    }

    @DeleteMapping("/result/details/{resultUuid}")
    public String removeResultDetails(@PathVariable String resultUuid, Model model) {
        model.addAttribute("result", cache.get(resultUuid));
        return "result-collapsed";
    }
}
