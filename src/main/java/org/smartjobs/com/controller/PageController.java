package org.smartjobs.com.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/")
    public String overview() {
        return "index";
    }


    @GetMapping("/analyze")
    public String getAnalysisPage() {
        return "analyze";
    }
}
