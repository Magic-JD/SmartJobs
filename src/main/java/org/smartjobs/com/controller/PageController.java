package org.smartjobs.com.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @Autowired
    PasswordEncoder passwordEncoder;

    @GetMapping("/")
    public String overview() {
        return "index";
    }


    @GetMapping("/analyze")
    public String getAnalysisPage() {
        return "analyze";
    }
}
