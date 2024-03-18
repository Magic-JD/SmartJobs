package org.smartjobs.com.controller.htmx;

import org.smartjobs.com.controller.candidate.CandidateController;
import org.smartjobs.com.service.candidate.CandidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Date;
import java.util.List;

@Controller
public class HomepageController {

    @Autowired
    private CandidateService candidateService;

    @GetMapping("/")
    public String overview(Model model){
        List<String> candidates = candidateService.getCurrentCandidates();
        model.addAttribute("candidates", candidates);
        return "index";
    }

}
