package org.smartjobs.adaptors.view.web.controller.analysis;

import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.smartjobs.core.entities.CandidateScores;
import org.smartjobs.core.entities.ProcessedCv;
import org.smartjobs.core.entities.User;
import org.smartjobs.core.exception.categories.UserResolvedExceptions.NoCandidatesSelectedException;
import org.smartjobs.core.exception.categories.UserResolvedExceptions.NoRoleSelectedException;
import org.smartjobs.core.exception.categories.UserResolvedExceptions.NotEnoughCreditException;
import org.smartjobs.core.service.AnalysisService;
import org.smartjobs.core.service.CandidateService;
import org.smartjobs.core.service.CreditService;
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
import java.util.concurrent.ConcurrentHashMap;

import static org.smartjobs.adaptors.view.web.constants.ThymeleafConstants.*;

@Controller
@Slf4j
@RequestMapping("/analysis")
public class AnalysisController {


    private final CandidateService candidateService;
    private final AnalysisService analysisService;

    private final RoleService roleService;

    private final CreditService creditService;

    private final ConcurrentHashMap<String, CandidateScores> cache = new ConcurrentHashMap<>();

    @Autowired
    public AnalysisController(
            CandidateService candidateService,
            AnalysisService analysisService,
            RoleService roleService,
            CreditService creditService) {
        this.candidateService = candidateService;
        this.analysisService = analysisService;
        this.roleService = roleService;
        this.creditService = creditService;
    }


    @HxRequest
    @GetMapping("/scoring")
    public String scoreAllCandidates(@AuthenticationPrincipal User user, HttpServletResponse response, Model model) {
        var userId = user.getId();
        var role = roleService.getCurrentlySelectedRole(userId).orElseThrow(() -> new NoRoleSelectedException(userId));
        List<ProcessedCv> candidateInformation = candidateService.getFullCandidateInfo(userId, role.id());
        //TODO sort out this mess!
        if (candidateInformation.isEmpty()) {
            throw new NoCandidatesSelectedException(userId);
        }
        if (!creditService.debitAndVerify(userId, candidateInformation.size())) {
            throw new NotEnoughCreditException(userId);
        }
        var results = analysisService.scoreToCriteria(userId, candidateInformation, role).stream()
                .sorted(Comparator.comparing(CandidateScores::percentage).reversed()).toList();
        results.forEach(result -> cache.put(result.uuid(), result));
        var failedCount = candidateInformation.size() - results.size();
        if (failedCount > 0) {
            creditService.refund(userId, failedCount);
        }
        model.addAttribute("results", results);
        return SCORING_FRAGMENT;
    }

    @HxRequest
    @GetMapping("/result/details/{resultUuid}")
    public String retrieveResultDetails(@PathVariable String resultUuid, Model model, HttpServletResponse response) {
        model.addAttribute("result", cache.get(resultUuid));
        return RESULT_DETAILS_FRAGMENT;
    }

    @HxRequest
    @DeleteMapping("/result/details/{resultUuid}")
    public String removeResultDetails(@PathVariable String resultUuid, Model model, HttpServletResponse response) {
        model.addAttribute("result", cache.get(resultUuid));
        return RESULT_COLLAPSED_FRAGMENT;
    }

}
