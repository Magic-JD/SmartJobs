package org.smartjobs.adaptors.view.web.controller.candidate;

import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.websocket.server.PathParam;
import org.smartjobs.core.entities.CandidateData;
import org.smartjobs.core.entities.Role;
import org.smartjobs.core.entities.User;
import org.smartjobs.core.exception.categories.UserResolvedExceptions.NoRoleSelectedException;
import org.smartjobs.core.service.CandidateService;
import org.smartjobs.core.service.CreditService;
import org.smartjobs.core.service.FileService;
import org.smartjobs.core.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;

import static org.smartjobs.adaptors.view.web.constants.EventConstants.CANDIDATE_COUNT_UPDATED;
import static org.smartjobs.adaptors.view.web.constants.HtmxConstants.HX_TRIGGER;
import static org.smartjobs.adaptors.view.web.constants.ThymeleafConstants.*;

@Controller
@RequestMapping("/candidate")
public class CandidateController {


    private final CandidateService candidateService;
    private final FileService fileService;
    private final CreditService creditService;
    private final RoleService roleService;



    @Autowired
    public CandidateController(FileService fileService,
                               CandidateService candidateService,
                               CreditService creditService,
                               RoleService roleService) {
        this.fileService = fileService;
        this.candidateService = candidateService;
        this.creditService = creditService;
        this.roleService = roleService;
    }

    @HxRequest
    @PostMapping("/upload")
    public String uploadFile(@AuthenticationPrincipal User user,
                             @RequestParam(name = "files") MultipartFile[] files,
                             Model model) {
        var userId = user.getId();
        creditService.debit(userId, files.length);
        var role = roleService.getCurrentlySelectedRole(userId).orElseThrow(() -> new NoRoleSelectedException(userId));
        var handledFiles = Arrays.stream(files)
                .map(fileService::handleFile)
                .distinct()
                .toList();
        var processedCvs = candidateService.updateCandidateCvs(userId, role.id(), handledFiles);
        var failedCount = files.length - processedCvs.size();
        if (failedCount > 0) {
            creditService.refund(userId, failedCount);
        }
        int selectedCount = candidateService.findSelectedCandidateCount(userId, role.id());

        model.addAttribute("selectedCount", selectedCount);
        model.addAttribute("currentRole", role.position());
        return CANDIDATE_PAGE;
    }


    @HxRequest
    @GetMapping()
    public String getAllCandidates(@AuthenticationPrincipal User user, Model model) {
        var userId = user.getId();
        var selectedRoleOptional = roleService.getCurrentlySelectedRoleId(userId);
        if (selectedRoleOptional.isEmpty()) {
            return EMPTY_FRAGMENT;
        }
        var selectedRole = selectedRoleOptional.get();
        var candidates = candidateService.getCurrentCandidates(userId, selectedRole).stream()
                .sorted(Comparator.comparing(CandidateData::name)).toList();
        model.addAttribute("candidates", candidates);
        return CANDIDATE_TABLE_FRAGMENT;
    }

    @HxRequest
    @DeleteMapping("/delete/{candidateId}")
    public String deleteCandidate(@AuthenticationPrincipal User user, @PathVariable long candidateId, HttpServletResponse response) {
        var userId = user.getId();
        var selectedRole = roleService.getCurrentlySelectedRoleId(userId).orElseThrow();
        candidateService.deleteCandidate(userId, selectedRole, candidateId);
        response.addHeader(HX_TRIGGER, CANDIDATE_COUNT_UPDATED);
        return EMPTY_FRAGMENT;
    }

    @HxRequest
    @DeleteMapping("/delete/all")
    public String deleteAllCandidates(@AuthenticationPrincipal User user, Model model, HttpServletResponse response) {
        var userId = user.getId();
        Long roleId = roleService.getCurrentlySelectedRoleId(userId).orElseThrow(() -> new NoRoleSelectedException(userId));
        candidateService.deleteAllCandidates(userId, roleId);
        response.addHeader(HX_TRIGGER, CANDIDATE_COUNT_UPDATED);
        model.addAttribute("candidates", Collections.emptyList());
        return CANDIDATE_TABLE_FRAGMENT;
    }

    @HxRequest
    @PutMapping("/select/{cvId}")
    public String selectCandidate(@AuthenticationPrincipal User user, @PathVariable long cvId, @PathParam("select") boolean select, Model model, HttpServletResponse response) {
        var userId = user.getId();
        response.addHeader(HX_TRIGGER, CANDIDATE_COUNT_UPDATED);
        long roleId = roleService.getCurrentlySelectedRoleId(userId).orElseThrow();
        return candidateService.toggleCandidateSelect(userId, roleId, cvId, select).map(cvData -> {
            model.addAttribute("candidate", cvData);
            return SINGLE_CANDIDATE_ROW_FRAGMENT;
        }).orElse(EMPTY_FRAGMENT);

    }

    @HxRequest
    @GetMapping("/number/selected")
    public String findNumberOfCandidatesSelected(@AuthenticationPrincipal User user, Model model) {
        var userId = user.getId();
        Optional<Role> role = roleService.getCurrentlySelectedRole(userId);
        var currentRole = role
                .map(Role::position)
                .orElse("NONE");
        int selectedCount = role
                .map(r -> candidateService.findSelectedCandidateCount(userId, r.id()))
                .orElse(0);

        model.addAttribute("selectedCount", selectedCount);
        model.addAttribute("currentRole", currentRole);
        return CANDIDATE_COUNT_FRAGMENT;
    }

}
