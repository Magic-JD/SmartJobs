package org.smartjobs.adaptors.view.web.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.smartjobs.adaptors.view.web.controller.roles.RolesController;
import org.smartjobs.adaptors.view.web.entities.NavElement;
import org.smartjobs.core.entities.Role;
import org.smartjobs.core.entities.User;
import org.smartjobs.core.service.CandidateService;
import org.smartjobs.core.service.CreditService;
import org.smartjobs.core.service.RoleService;
import org.smartjobs.core.service.role.data.CriteriaCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.text.DecimalFormat;
import java.util.*;

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
    public String overview(@AuthenticationPrincipal User user, Model model) {
        setAllowedNavigationForUser(user, model);
        return LANDING_PAGE;
    }


    @GetMapping("/analyze")
    public String getAnalysisPage(@AuthenticationPrincipal User user, Model model) {
        addInfoBoxInfo(user, model);
        return ANALYSIS_PAGE;
    }


    @GetMapping("/candidates")
    public String getCandidatesPage(@AuthenticationPrincipal User user, Model model) {
        addInfoBoxInfo(user, model);
        return CANDIDATE_PAGE;
    }

    @GetMapping("/upload")
    public String getUploadPage(@AuthenticationPrincipal User user, Model model) {
        return UPLOAD_PAGE;
    }

    @GetMapping("/roles")
    public String getRolesPage(@AuthenticationPrincipal User user, HttpServletResponse response, Model model) {
        var userId = user.getId();
        Optional<Role> currentlySelectedRole = roleService.getCurrentlySelectedRole(userId);
        Long currentlySelected = currentlySelectedRole.map(Role::id).orElse(0L);
        CriteriaCategory[] values = CriteriaCategory.values();
        var role = currentlySelectedRole.map(internalRole -> new RolesController.Role(
                internalRole.id(),
                internalRole.position(),
                Arrays.stream(values).map(cc ->
                                new RolesController.Category(
                                        cc.toString(),
                                        internalRole.scoringCriteria()
                                                .stream()
                                                .filter(crit -> crit.category().equals(cc))
                                                .map(crit -> new RolesController.ScoringCriteria(crit.id(), crit.name(), crit.weighting()))
                                                .toList()
                                )
                        )
                        .toList())
        );

        var categoryNames = Arrays.stream(CriteriaCategory.values()).map(CriteriaCategory::toString).sorted().toList();
        model.addAttribute("savedRoles", roleService.getUserRoles(userId));
        model.addAttribute("currentlySelected", currentlySelected);
        model.addAttribute("role", role.orElse(null));
        model.addAttribute("categories", categoryNames);
        return ROLES_PAGE;

    }

    private void setAllowedNavigationForUser(User user, Model model) {
        if (user == null) {
            model.addAllAttributes(Map.of(
                    "loggedIn", false,
                    "navElements", Collections.emptyList()));
            return;
        }
        model.addAllAttributes(Map.of(
                "credits", decimalFormat.format(creditService.userCredit(user.getId())),
                "loggedIn", true,
                "navElements", List.of(
                        new NavElement("roles", "Roles", false),
                        new NavElement("candidates", "Candidates", false))));
    }

    private void addInfoBoxInfo(User user, Model model) {
        long userId = user.getId();
        Optional<Role> role = roleService.getCurrentlySelectedRoleId(userId)
                .map(roleService::getRole);
        var currentRole = role
                .map(Role::position)
                .orElse("NONE");
        int selectedCount = role.map(role1 -> candidateService.findSelectedCandidateCount(userId, role1.id())).orElse(0);

        model.addAttribute("selectedCount", selectedCount);
        model.addAttribute("currentRole", currentRole);
    }
}
