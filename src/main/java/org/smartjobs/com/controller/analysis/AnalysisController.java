package org.smartjobs.com.controller.analysis;

import jakarta.servlet.http.HttpServletResponse;
import org.smartjobs.com.client.gpt.GptClient;
import org.smartjobs.com.service.analysis.AnalysisService;
import org.smartjobs.com.service.auth.AuthService;
import org.smartjobs.com.service.candidate.CandidateService;
import org.smartjobs.com.service.candidate.data.ProcessedCv;
import org.smartjobs.com.service.chroma.ChromaService;
import org.smartjobs.com.service.role.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static org.smartjobs.com.constants.ThymeleafConstants.*;
import static org.smartjobs.com.exception.message.ExceptionMessage.createUserErrorMessageToDisplayForUser;

@Controller
@RequestMapping("/analysis")
public class AnalysisController {


    private final CandidateService candidateService;
    private final AnalysisService analysisService;
    private final AuthService authService;

    private final RoleService roleService;

    private final ChromaService chromaService;

    private final ConcurrentHashMap<String, GptClient.ScoringCriteriaResult> cache = new ConcurrentHashMap<>();

    @Autowired
    public AnalysisController(
            CandidateService candidateService,
            AnalysisService analysisService,
            AuthService authService,
            RoleService roleService, ChromaService chromaService) {
        this.candidateService = candidateService;
        this.analysisService = analysisService;
        this.authService = authService;
        this.roleService = roleService;
        this.chromaService = chromaService;
    }


    public record ChromaContext(String name, List<String> relevantContext){}

    @GetMapping("/scoring")
    public String scoreAllCandidates(HttpServletResponse response, Model model) {
        String username = authService.getCurrentUsername();
        List<ProcessedCv> candidateInformation = candidateService.getFullCandidateInfo(username);
        if (candidateInformation.isEmpty()) {
            return createUserErrorMessageToDisplayForUser("Please upload some users to analyze.", response, model);
        }
        var role = roleService.getRole();
        var results = analysisService.scoreToCriteria(candidateInformation, role).stream()
                .sorted(Comparator.comparing(GptClient.ScoringCriteriaResult::percentage).reversed()).toList();
        results.forEach(result -> cache.put(result.uuid(), result));
        model.addAttribute("results", results);
        return SCORING_FRAGMENT;
    }

    @GetMapping("/result/details/{resultUuid}")
    public String retrieveResultDetails(@PathVariable String resultUuid, Model model) {
        model.addAttribute("result", cache.get(resultUuid));
        return RESULT_DETAILS_FRAGMENT;
    }

    @DeleteMapping("/result/details/{resultUuid}")
    public String removeResultDetails(@PathVariable String resultUuid, Model model) {
        model.addAttribute("result", cache.get(resultUuid));
        return RESULT_COLLAPSED_FRAGMENT;
    }
}
