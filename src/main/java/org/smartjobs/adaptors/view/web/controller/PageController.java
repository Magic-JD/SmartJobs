package org.smartjobs.adaptors.view.web.controller;

import org.smartjobs.adaptors.view.web.entities.NavElement;
import org.smartjobs.core.service.AuthService;
import org.smartjobs.core.service.auth.levels.AuthLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.smartjobs.adaptors.view.web.constants.ThymeleafConstants.*;

@Controller
public class PageController {


    private final AuthService authService;

    @Autowired
    public PageController(AuthService authService) {
        this.authService = authService;
    }


    @GetMapping("/")
    public String overview(Model model) {
        setAllowedNavigationForUser(model);
        return LANDING_PAGE;
    }


    @GetMapping("/analyze")
    public String getAnalysisPage(Model model) {
        setAllowedNavigationForUser(model);
        return ANALYSIS_PAGE;
    }

    @GetMapping("/candidates")
    public String getCandidatesPage(Model model) {
        setAllowedNavigationForUser(model);
        return CANDIDATE_PAGE;
    }

    @GetMapping("/upload")
    public String getUploadPage(Model model) {
        setAllowedNavigationForUser(model);
        return UPLOAD_PAGE;
    }

    @GetMapping("/roles")
    public String getRolesPage(Model model) {
        setAllowedNavigationForUser(model);
        return ROLES_PAGE;

    }

    private void setAllowedNavigationForUser(Model model) {
        AuthLevel authLevel = authService.userMaxAuthLevel();
        switch (authLevel) { //The switch statement should be preferred for enums - forced update when new enum added
            case ADMIN, USER -> model.addAllAttributes(Map.of(
                    "loggedIn", true,
                    "navElements", List.of(
                            new NavElement(ROLES_PAGE, "Roles", false),
                            new NavElement(CANDIDATE_PAGE, "Candidates", false))));
            case ROLE_ANONYMOUS -> model.addAllAttributes(Map.of(
                    "loggedIn", false,
                    "navElements", Collections.emptyList()));

        }
    }
}
