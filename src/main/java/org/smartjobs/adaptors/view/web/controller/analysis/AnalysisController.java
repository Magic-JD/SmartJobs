package org.smartjobs.adaptors.view.web.controller.analysis;

import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.smartjobs.core.entities.CandidateScores;
import org.smartjobs.core.entities.ProcessedCv;
import org.smartjobs.core.entities.User;
import org.smartjobs.core.exception.categories.UserResolvedExceptions.NoRoleSelectedException;
import org.smartjobs.core.service.AnalysisService;
import org.smartjobs.core.service.CandidateService;
import org.smartjobs.core.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Comparator;
import java.util.List;

import static org.smartjobs.adaptors.view.web.constants.ThymeleafConstants.*;

@Controller
@Slf4j
@RequestMapping("/analysis")
public class AnalysisController {


    private final CandidateService candidateService;
    private final AnalysisService analysisService;

    private final RoleService roleService;

    @Autowired
    public AnalysisController(
            CandidateService candidateService,
            AnalysisService analysisService,
            RoleService roleService) {
        this.candidateService = candidateService;
        this.analysisService = analysisService;
        this.roleService = roleService;
    }


    @HxRequest
    @GetMapping("/scoring")
    public String scoreAllCandidates(@AuthenticationPrincipal User user, HttpServletResponse response, Model model) {
        var userId = user.getId();
        var role = roleService.getCurrentlySelectedRole(userId).orElseThrow(() -> new NoRoleSelectedException(userId));
        List<ProcessedCv> candidateInformation = candidateService.getFullCandidateInfo(userId, role.id());

        var results = analysisService.scoreToCriteria(userId, role.id(), candidateInformation, role.scoringCriteria()).stream()
                .sorted(Comparator.comparing(CandidateScores::percentage).reversed()).toList();
        model.addAttribute("results", results);
        return SCORING_FRAGMENT;
    }

    @HxRequest
    @GetMapping("/result/details/{resultId}")
    public String retrieveResultDetails(@PathVariable Long resultId, Model model, HttpServletResponse response) {
        model.addAttribute("result", analysisService.getResultById(resultId));
        return RESULT_DETAILS_FRAGMENT;
    }

    @HxRequest
    @DeleteMapping("/result/details/{resultId}")
    public String removeResultDetails(@PathVariable Long resultId, Model model, HttpServletResponse response) {
        model.addAttribute("result", analysisService.getResultById(resultId));
        return RESULT_COLLAPSED_FRAGMENT;
    }

}
