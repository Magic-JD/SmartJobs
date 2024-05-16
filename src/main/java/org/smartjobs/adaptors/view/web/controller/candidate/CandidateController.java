package org.smartjobs.adaptors.view.web.controller.candidate;

import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.websocket.server.PathParam;
import org.smartjobs.core.entities.CandidateData;
import org.smartjobs.core.entities.Role;
import org.smartjobs.core.exception.categories.UserResolvedExceptions.NoRoleSelectedException;
import org.smartjobs.core.exception.categories.UserResolvedExceptions.NotEnoughCreditException;
import org.smartjobs.core.service.*;
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
import static org.smartjobs.adaptors.view.web.constants.HtmxConstants.HX_TRIGGER;
import static org.smartjobs.adaptors.view.web.constants.ThymeleafConstants.*;

@Controller
@RequestMapping("/candidate")
public class CandidateController {


    private final CandidateService candidateService;
    private final FileService fileService;
    private final CreditService creditService;
    private final RoleService roleService;
    private final SseService sseService;



    @Autowired
    public CandidateController(FileService fileService,
                               CandidateService candidateService,
                               CreditService creditService,
                               RoleService roleService,
                               SseService sseService) {
        this.fileService = fileService;
        this.candidateService = candidateService;
        this.creditService = creditService;
        this.roleService = roleService;
        this.sseService = sseService;
    }

    @HxRequest
    @PostMapping("/upload")
    public String uploadFile(@AuthenticationPrincipal UserDetails userDetails,
                             @RequestParam(name = "files") MultipartFile[] files,
                             Model model) {
        String username = userDetails.getUsername();
        sseService.send(username, "progress-upload", STR. "<div>Uploaded: 0/\{ files.length }</div>" );
        if (!creditService.debitAndVerify(username, files.length)) {
            throw new NotEnoughCreditException();
        }
        var handledFiles = Arrays.stream(files)
                .map(fileService::handleFile)
                .toList();
        var role = roleService.getCurrentlySelectedRole(username);
        role.ifPresent(r -> candidateService.updateCandidateCvs(username, r.id(), handledFiles));
        var currentRole = role
                .map(Role::position)
                .orElse("NONE");
        int selectedCount = role
                .map(r -> candidateService.findSelectedCandidateCount(username, r.id()))
                .orElse(0);

        model.addAttribute("selectedCount", selectedCount);
        model.addAttribute("currentRole", currentRole);
        return CANDIDATE_PAGE;
    }


    @HxRequest
    @GetMapping()
    public String getAllCandidates(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        String currentUsername = userDetails.getUsername();
        var selectedRoleOptional = roleService.getCurrentlySelectedRoleId(currentUsername);
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
        var selectedRole = roleService.getCurrentlySelectedRoleId(currentUsername).orElseThrow();
        candidateService.deleteCandidate(currentUsername, selectedRole, cvId);
        response.addHeader(HX_TRIGGER, CANDIDATE_COUNT_UPDATED);
        return EMPTY_FRAGMENT;
    }

    @HxRequest
    @DeleteMapping("/delete/all")
    public String deleteAllCandidates(@AuthenticationPrincipal UserDetails userDetails, Model model, HttpServletResponse response) {
        String currentUsername = userDetails.getUsername();
        String username = userDetails.getUsername();
        Long roleId = roleService.getCurrentlySelectedRoleId(username).orElseThrow(NoRoleSelectedException::new);
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
        long roleId = roleService.getCurrentlySelectedRoleId(currentUsername).orElseThrow();
        return candidateService.toggleCandidateSelect(currentUsername, roleId, cvId, select).map(cvData -> {
            model.addAttribute("candidate", cvData);
            return SINGLE_CANDIDATE_ROW_FRAGMENT;
        }).orElse(EMPTY_FRAGMENT);

    }

    @HxRequest
    @GetMapping("/number/selected")
    public String findNumberOfCandidatesSelected(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        String username = userDetails.getUsername();
        Optional<Role> role = roleService.getCurrentlySelectedRole(username);
        var currentRole = role
                .map(Role::position)
                .orElse("NONE");
        int selectedCount = role
                .map(r -> candidateService.findSelectedCandidateCount(username, r.id()))
                .orElse(0);

        model.addAttribute("selectedCount", selectedCount);
        model.addAttribute("currentRole", currentRole);
        return CANDIDATE_COUNT_FRAGMENT;
    }

}
