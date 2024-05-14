package org.smartjobs.adaptors.view.web.controller;

import org.smartjobs.adaptors.view.web.entities.NavElement;
import org.smartjobs.core.service.CreditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.smartjobs.adaptors.view.web.constants.ThymeleafConstants.*;

@Controller
public class PageController {


    private final CreditService creditService;

    @Autowired
    public PageController(CreditService creditService) {
        this.creditService = creditService;
    }


    @GetMapping("/")
    public String overview(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        setAllowedNavigationForUser(userDetails, model);
        return LANDING_PAGE;
    }


    @GetMapping("/analyze")
    public String getAnalysisPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        return ANALYSIS_PAGE;
    }

    @GetMapping("/candidates")
    public String getCandidatesPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
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
                    "credits", -1,
                    "loggedIn", false,
                    "navElements", Collections.emptyList()));
            return;
        }
        model.addAllAttributes(Map.of(
                "credits", creditService.userCredit(userDetails.getUsername()),
                "loggedIn", true,
                "navElements", List.of(
                        new NavElement(ROLES_PAGE, "Roles", false),
                        new NavElement(CANDIDATE_PAGE, "Candidates", false))));
    }
}
