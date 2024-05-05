package org.smartjobs.com.adaptors.view.web.controller.candidate;

import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.websocket.server.PathParam;
import org.smartjobs.com.core.entities.CandidateData;
import org.smartjobs.com.core.entities.Role;
import org.smartjobs.com.core.exception.categories.UserResolvedExceptions.NoRoleSelectedException;
import org.smartjobs.com.core.exception.categories.UserResolvedExceptions.NotEnoughCreditException;
import org.smartjobs.com.core.service.auth.AuthService;
import org.smartjobs.com.core.service.candidate.CandidateService;
import org.smartjobs.com.core.service.credit.CreditService;
import org.smartjobs.com.core.service.file.FileService;
import org.smartjobs.com.core.service.role.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;

import static org.smartjobs.com.adaptors.view.web.constants.EventConstants.CANDIDATE_COUNT_UPDATED;
import static org.smartjobs.com.adaptors.view.web.constants.HtmxConstants.HX_REDIRECT;
import static org.smartjobs.com.adaptors.view.web.constants.HtmxConstants.HX_TRIGGER;
import static org.smartjobs.com.adaptors.view.web.constants.ThymeleafConstants.*;

@Controller
@RequestMapping("/candidate")
public class CandidateController {


    private final CandidateService candidateService;
    private final FileService fileService;
    private final CreditService creditService;
    private final AuthService authService;
    private final RoleService roleService;



    @Autowired
    public CandidateController(FileService fileService, CandidateService candidateService, CreditService creditService, AuthService authService, RoleService roleService) {
        this.fileService = fileService;
        this.candidateService = candidateService;
        this.creditService = creditService;
        this.authService = authService;
        this.roleService = roleService;
    }

    @HxRequest
    @PostMapping("/upload")
    public String uploadFile(@RequestParam(name = "files") MultipartFile[] files, HttpServletResponse response) {
        String currentUsername = authService.getCurrentUsername();
        if (!creditService.userHasEnoughCredits(currentUsername)) {
            throw new NotEnoughCreditException();
        }
        var handledFiles = Arrays.stream(files)
                .map(fileService::handleFile)
                .toList();
        Long roleId = roleService.getCurrentlySelectedRole(currentUsername)
                .orElseThrow(NoRoleSelectedException::new);
        candidateService.updateCandidateCvs(currentUsername, roleId, handledFiles);
        response.addHeader(HX_REDIRECT, "/candidates");
        return EMPTY_FRAGMENT;
    }


    @HxRequest
    @GetMapping()
    public String getAllCandidates(Model model) {
        String currentUsername = authService.getCurrentUsername();
        var selectedRoleOptional = roleService.getCurrentlySelectedRole(currentUsername);
        if (selectedRoleOptional.isEmpty()) {
            return EMPTY_FRAGMENT;
        }
        var selectedRole = selectedRoleOptional.get();
        var candidates = candidateService.getCurrentCandidates(currentUsername, selectedRole).stream()
                .sorted(Comparator.comparing(CandidateData::name)).toList();
        model.addAttribute("candidates", candidates);
        return CANDIDATE_TABLE_FRAGMENT;
    }

    @HxRequest
    @DeleteMapping("/delete/{cvId}")
    public String deleteCandidate(@PathVariable long cvId, HttpServletResponse response) {
        String currentUsername = authService.getCurrentUsername();
        candidateService.deleteCandidate(currentUsername, cvId);
        response.addHeader(HX_TRIGGER, CANDIDATE_COUNT_UPDATED);
        return EMPTY_FRAGMENT;
    }

    @HxRequest
    @DeleteMapping("/delete/all")
    public String deleteAllCandidates(Model model, HttpServletResponse response) {
        String currentUsername = authService.getCurrentUsername();
        String username = authService.getCurrentUsername();
        Long roleId = roleService.getCurrentlySelectedRole(username).orElseThrow(NoRoleSelectedException::new);
        candidateService.deleteAllCandidates(currentUsername, roleId);
        response.addHeader(HX_TRIGGER, CANDIDATE_COUNT_UPDATED);
        model.addAttribute("candidates", Collections.emptyList());
        return CANDIDATE_TABLE_FRAGMENT;
    }

    @HxRequest
    @PutMapping("/select/{cvId}")
    public String selectCandidate(@PathVariable long cvId, @PathParam("select") boolean select, Model model, HttpServletResponse response) {
        String currentUsername = authService.getCurrentUsername();
        response.addHeader(HX_TRIGGER, CANDIDATE_COUNT_UPDATED);
        return candidateService.toggleCandidateSelect(currentUsername, cvId, select).map(cvData -> {
            model.addAttribute("candidate", cvData);
            return SINGLE_CANDIDATE_ROW_FRAGMENT;
        }).orElse(EMPTY_FRAGMENT);

    }

    @HxRequest
    @GetMapping("/number/selected")
    public String findNumberOfCandidatesSelected(Model model) {
        String username = authService.getCurrentUsername();
        Optional<Role> role = roleService.getCurrentlySelectedRole(username)
                .map(roleService::getRole);
        var currentRole = role
                .map(Role::position)
                .orElse("NONE");
        int selectedCount = role.map(role1 -> candidateService.findSelectedCandidateCount(username, role1.id())).orElse(0);

        model.addAttribute("selectedCount", selectedCount);
        model.addAttribute("currentRole", currentRole);
        return CANDIDATE_COUNT_FRAGMENT;
    }

}
