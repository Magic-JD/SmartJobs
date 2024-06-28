package org.smartjobs.adaptors.view.web.controller.candidate;

import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.websocket.server.PathParam;
import org.smartjobs.core.entities.CandidateData;
import org.smartjobs.core.entities.Role;
import org.smartjobs.core.entities.User;
import org.smartjobs.core.exception.categories.UserResolvedExceptions.NoRoleSelectedException;
import org.smartjobs.core.service.CandidateService;
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
    private final RoleService roleService;



    @Autowired
    public CandidateController(CandidateService candidateService,
                               RoleService roleService) {
        this.candidateService = candidateService;
        this.roleService = roleService;
    }

    @HxRequest
    @PostMapping("/upload")
    public String uploadFile(@AuthenticationPrincipal User user,
                             @RequestParam(name = "files") MultipartFile[] files,
                             Model model) {
        var userId = user.getId();
        var role = roleService.getCurrentlySelectedRole(userId).orElseThrow(() -> new NoRoleSelectedException(userId));
        candidateService.updateCandidateCvs(userId, role.id(), Arrays.stream(files).toList());
        int selectedCount = candidateService.findSelectedCandidateCount(userId, role.id());

        model.addAttribute("selectedCount", selectedCount);
        model.addAttribute("currentRole", role.position());
        return CANDIDATE_PAGE;
    }


    @HxRequest
    @GetMapping()
    public String getAllCandidates(@AuthenticationPrincipal User user, Model model) {
        var userId = user.getId();
        var candidates = roleService.getCurrentlySelectedRoleId(userId)
                .map(roleId -> candidateService.getCurrentCandidates(userId, roleId))
                .orElse(Collections.emptyList());
        var sorted = candidates.stream().sorted(Comparator.comparing(CandidateData::name)).toList();
        model.addAttribute("candidates", sorted);
        return CANDIDATE_TABLE_FRAGMENT;
    }

    @HxRequest
    @DeleteMapping("/delete/{candidateId}")
    public String deleteCandidate(@AuthenticationPrincipal User user, @PathVariable long candidateId, HttpServletResponse response) {
        var userId = user.getId();
        var roleId = roleService.getCurrentlySelectedRoleId(userId).orElseThrow(() -> new NoRoleSelectedException(userId));
        candidateService.deleteCandidate(userId, roleId, candidateId);
        response.addHeader(HX_TRIGGER, CANDIDATE_COUNT_UPDATED);
        return EMPTY_FRAGMENT;
    }

    @HxRequest
    @DeleteMapping("/delete/all")
    public String deleteAllCandidates(@AuthenticationPrincipal User user, Model model, HttpServletResponse response) {
        var userId = user.getId();
        var roleId = roleService.getCurrentlySelectedRoleId(userId).orElseThrow(() -> new NoRoleSelectedException(userId));
        candidateService.deleteAllCandidates(userId, roleId);
        var candidates = candidateService.getCurrentCandidates(userId, roleId);
        var sorted = candidates.stream().sorted(Comparator.comparing(CandidateData::name)).toList();
        response.addHeader(HX_TRIGGER, CANDIDATE_COUNT_UPDATED);
        model.addAttribute("candidates", sorted);
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
    @PutMapping("/select/all")
    public String selectAllCandidates(@AuthenticationPrincipal User user, @PathParam("select") boolean select, Model model, HttpServletResponse response) {
        var userId = user.getId();
        response.addHeader(HX_TRIGGER, CANDIDATE_COUNT_UPDATED);
        long roleId = roleService.getCurrentlySelectedRoleId(userId).orElseThrow();
        var candidates = candidateService.toggleCandidateSelectAll(userId, roleId, select);
        var sorted = candidates.stream().sorted(Comparator.comparing(CandidateData::name)).toList();
        model.addAttribute("candidates", sorted);
        return CANDIDATE_TABLE_FRAGMENT;

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
