package org.smartjobs.adaptors.view.web.controller.candidate;

import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.websocket.server.PathParam;
import org.smartjobs.core.entities.CandidateData;
import org.smartjobs.core.entities.Role;
import org.smartjobs.core.exception.categories.UserResolvedExceptions.NoRoleSelectedException;
import org.smartjobs.core.exception.categories.UserResolvedExceptions.NotEnoughCreditException;
import org.smartjobs.core.service.CandidateService;
import org.smartjobs.core.service.CreditService;
import org.smartjobs.core.service.FileService;
import org.smartjobs.core.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;

import static org.smartjobs.adaptors.view.web.constants.EventConstants.CANDIDATE_COUNT_UPDATED;
import static org.smartjobs.adaptors.view.web.constants.HtmxConstants.HX_REDIRECT;
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
    public CandidateController(FileService fileService, CandidateService candidateService, CreditService creditService, RoleService roleService) {
        this.fileService = fileService;
        this.candidateService = candidateService;
        this.creditService = creditService;
        this.roleService = roleService;
    }

    @HxRequest
    @PostMapping("/upload")
    public String uploadFile(@AuthenticationPrincipal UserDetails userDetails, @RequestParam(name = "files") MultipartFile[] files, HttpServletResponse response) {
        String username = userDetails.getUsername();
        if (!creditService.debitAndVerify(username, files.length)) {
            throw new NotEnoughCreditException();
        }
        var handledFiles = Arrays.stream(files)
                .map(fileService::handleFile)
                .toList();
        Long roleId = roleService.getCurrentlySelectedRole(username)
                .orElseThrow(NoRoleSelectedException::new);
        candidateService.updateCandidateCvs(username, roleId, handledFiles);
        response.addHeader(HX_REDIRECT, "/candidates");
        return EMPTY_FRAGMENT;
    }


    @HxRequest
    @GetMapping()
    public String getAllCandidates(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        String currentUsername = userDetails.getUsername();
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
    public String deleteCandidate(@AuthenticationPrincipal UserDetails userDetails, @PathVariable long cvId, HttpServletResponse response) {
        String currentUsername = userDetails.getUsername();
        var selectedRole = roleService.getCurrentlySelectedRole(currentUsername).orElseThrow();
        candidateService.deleteCandidate(currentUsername, selectedRole, cvId);
        response.addHeader(HX_TRIGGER, CANDIDATE_COUNT_UPDATED);
        return EMPTY_FRAGMENT;
    }

    @HxRequest
    @DeleteMapping("/delete/all")
    public String deleteAllCandidates(@AuthenticationPrincipal UserDetails userDetails, Model model, HttpServletResponse response) {
        String currentUsername = userDetails.getUsername();
        String username = userDetails.getUsername();
        Long roleId = roleService.getCurrentlySelectedRole(username).orElseThrow(NoRoleSelectedException::new);
        candidateService.deleteAllCandidates(currentUsername, roleId);
        response.addHeader(HX_TRIGGER, CANDIDATE_COUNT_UPDATED);
        model.addAttribute("candidates", Collections.emptyList());
        return CANDIDATE_TABLE_FRAGMENT;
    }

    @HxRequest
    @PutMapping("/select/{cvId}")
    public String selectCandidate(@AuthenticationPrincipal UserDetails userDetails, @PathVariable long cvId, @PathParam("select") boolean select, Model model, HttpServletResponse response) {
        String currentUsername = userDetails.getUsername();
        response.addHeader(HX_TRIGGER, CANDIDATE_COUNT_UPDATED);
        long roleId = roleService.getCurrentlySelectedRole(currentUsername).orElseThrow();
        return candidateService.toggleCandidateSelect(currentUsername, roleId, cvId, select).map(cvData -> {
            model.addAttribute("candidate", cvData);
            return SINGLE_CANDIDATE_ROW_FRAGMENT;
        }).orElse(EMPTY_FRAGMENT);

    }

    @HxRequest
    @GetMapping("/number/selected")
    public String findNumberOfCandidatesSelected(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        String username = userDetails.getUsername();
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
