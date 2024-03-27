package org.smartjobs.com.controller;

import org.smartjobs.com.service.auth.AuthService;
import org.smartjobs.com.service.auth.levels.AuthLevel;
import org.smartjobs.com.ui.NavElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collections;
import java.util.List;
import java.util.Map;

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
        return "index";
    }


    @GetMapping("/analyze")
    public String getAnalysisPage(Model model) {
        setAllowedNavigationForUser(model);
        return "analyze";
    }

    @GetMapping("/candidates")
    public String getCandidatesPage(Model model) {
        setAllowedNavigationForUser(model);
        return "candidates";
    }

    @GetMapping("/upload")
    public String getUploadPage(Model model) {
        setAllowedNavigationForUser(model);
        return "upload";
    }

    private void setAllowedNavigationForUser(Model model) {
        AuthLevel authLevel = authService.userMaxAuthLevel();
        switch (authLevel) {
            case ADMIN, USER -> model.addAllAttributes(Map.of(
                    "loggedIn", true,
                    "navElements", List.of(
                            new NavElement("candidates", "Candidates", false),
                            new NavElement("analyze", "Analyze", false))));
            case ROLE_ANONYMOUS -> model.addAllAttributes(Map.of(
                    "loggedIn", false,
                    "navElements", Collections.emptyList()));

        }
    }
}
