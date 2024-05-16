package org.smartjobs.adaptors.view.web.controller;

import org.smartjobs.adaptors.view.web.entities.NavElement;
import org.smartjobs.core.entities.Role;
import org.smartjobs.core.service.CandidateService;
import org.smartjobs.core.service.CreditService;
import org.smartjobs.core.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.smartjobs.adaptors.view.web.constants.ThymeleafConstants.*;

@Controller
public class PageController {


    private final CreditService creditService;
    private final RoleService roleService;
    private final CandidateService candidateService;
    private final DecimalFormat decimalFormat;

    @Autowired
    public PageController(CreditService creditService, RoleService roleService, CandidateService candidateService, DecimalFormat decimalFormat) {
        this.creditService = creditService;
        this.roleService = roleService;
        this.candidateService = candidateService;
        this.decimalFormat = decimalFormat;
    }


    @GetMapping("/")
    public String overview(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        setAllowedNavigationForUser(userDetails, model);
        return LANDING_PAGE;
    }


    @GetMapping("/analyze")
    public String getAnalysisPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        addInfoBoxInfo(userDetails, model);
        return ANALYSIS_PAGE;
    }


    @GetMapping("/candidates")
    public String getCandidatesPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        addInfoBoxInfo(userDetails, model);
        return CANDIDATE_PAGE;
    }

    @GetMapping("/upload")
    public String getUploadPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        return UPLOAD_PAGE;
    }

    @GetMapping("/roles")
    public String getRolesPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        return ROLES_PAGE;

    }

    private void setAllowedNavigationForUser(UserDetails userDetails, Model model) {
        if (userDetails == null) {
            model.addAllAttributes(Map.of(
                    "loggedIn", false,
                    "navElements", Collections.emptyList()));
            return;
        }
        model.addAllAttributes(Map.of(
                "credits", decimalFormat.format(creditService.userCredit(userDetails.getUsername())),
                "loggedIn", true,
                "navElements", List.of(
                        new NavElement("roles", "Roles", false),
                        new NavElement("candidates", "Candidates", false))));
    }

    private void addInfoBoxInfo(UserDetails userDetails, Model model) {
        String username = userDetails.getUsername();
        Optional<Role> role = roleService.getCurrentlySelectedRole(username)
                .map(roleService::getRole);
        var currentRole = role
                .map(Role::position)
                .orElse("NONE");
        int selectedCount = role.map(role1 -> candidateService.findSelectedCandidateCount(username, role1.id())).orElse(0);

        model.addAttribute("selectedCount", selectedCount);
        model.addAttribute("currentRole", currentRole);
    }
}
