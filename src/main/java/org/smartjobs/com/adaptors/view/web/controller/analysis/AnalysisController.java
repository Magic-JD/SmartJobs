package org.smartjobs.com.adaptors.view.web.controller.analysis;

import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.smartjobs.com.core.entities.ProcessedCv;
import org.smartjobs.com.core.entities.ScoringCriteriaResult;
import org.smartjobs.com.core.exception.categories.UserResolvedExceptions;
import org.smartjobs.com.core.service.AnalysisService;
import org.smartjobs.com.core.service.AuthService;
import org.smartjobs.com.core.service.CandidateService;
import org.smartjobs.com.core.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static org.smartjobs.com.adaptors.view.web.constants.ThymeleafConstants.*;
import static org.smartjobs.com.adaptors.view.web.exception.message.ExceptionMessage.createUserErrorMessageToDisplayForUser;

@Controller
@RequestMapping("/analysis")
public class AnalysisController {


    private final CandidateService candidateService;
    private final AnalysisService analysisService;
    private final AuthService authService;

    private final RoleService roleService;

    private final ConcurrentHashMap<String, ScoringCriteriaResult> cache = new ConcurrentHashMap<>();

    @Autowired
    public AnalysisController(
            CandidateService candidateService,
            AnalysisService analysisService,
            AuthService authService,
            RoleService roleService) {
        this.candidateService = candidateService;
        this.analysisService = analysisService;
        this.authService = authService;
        this.roleService = roleService;
    }


    @HxRequest
    @GetMapping("/scoring")
    public String scoreAllCandidates(HttpServletResponse response, Model model) {
        String username = authService.getCurrentUsername();
        var roleId = roleService.getCurrentlySelectedRole(username).orElseThrow(UserResolvedExceptions.NoRoleSelectedException::new);
        List<ProcessedCv> candidateInformation = candidateService.getFullCandidateInfo(username, roleId);
        if (candidateInformation.isEmpty()) {
            return createUserErrorMessageToDisplayForUser("Please upload some users to analyze.", response, model);
        }
        Optional<Long> currentlySelectedRole = roleService.getCurrentlySelectedRole(username);
        if (currentlySelectedRole.isEmpty()) {
            return createUserErrorMessageToDisplayForUser("Please select a role", response, model);
        }
        var role = roleService.getRole(currentlySelectedRole.get());
        var results = analysisService.scoreToCriteria(candidateInformation, role).stream()
                .sorted(Comparator.comparing(ScoringCriteriaResult::percentage).reversed()).toList();
        results.forEach(result -> cache.put(result.uuid(), result));
        model.addAttribute("results", results);
        return SCORING_FRAGMENT;
    }

    @HxRequest
    @GetMapping("/result/details/{resultUuid}")
    public String retrieveResultDetails(@PathVariable String resultUuid, Model model) {
        model.addAttribute("result", cache.get(resultUuid));
        return RESULT_DETAILS_FRAGMENT;
    }

    @HxRequest
    @DeleteMapping("/result/details/{resultUuid}")
    public String removeResultDetails(@PathVariable String resultUuid, Model model) {
        model.addAttribute("result", cache.get(resultUuid));
        return RESULT_COLLAPSED_FRAGMENT;
    }
}
