package org.smartjobs.com.controller.htmx;

import org.smartjobs.com.controller.candidate.CandidateController;
import org.smartjobs.com.controller.listing.ListingController;
import org.smartjobs.com.controller.listing.request.MatchRequest;
import org.smartjobs.com.controller.listing.response.MatchResponse;
import org.smartjobs.com.controller.listing.response.TopMatch;
import org.smartjobs.com.service.candidate.CandidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/htmx")
public class HtmxController {

    private final CandidateController candidateController;

    private final CandidateService candidateService;
    private final ListingController listingController;

    @Autowired
    public HtmxController(ListingController listingController, CandidateController candidateController, CandidateService candidateService) {
        this.candidateController = candidateController;
        this.listingController = listingController;
        this.candidateService = candidateService;
    }

    @PostMapping("/candidate/upload")
    public String uploadFile(Model model, @RequestParam(name = "files") MultipartFile[] files
    ) {
        HttpStatus httpStatus = candidateController.uploadFile(files);
        List<String> candidates = candidateService.getCurrentCandidates();
        model.addAttribute("candidates", candidates);
        return "table";
    }

    @PostMapping("/listing/match")
    public String evaluateCandidate(@RequestParam String listing, Model model) {
        MatchResponse response = listingController.evaluateCandidate(new MatchRequest(listing)).getBody();
        List<TopMatch> topMatches = response.topMatches();
        String topScorers = topMatches.stream()
                .map(tm -> STR. "Name: \{ tm.name() } - Percentage Match: \{ tm.matchPercentage() }%" )
                .collect(Collectors.joining("<br>"));
        String topScorerName = topMatches.getFirst().name();
        String gptResponse = response.justification();
        String justification = STR."""
        <p>
        The top five candidates were
        <br>
        <br>
        \{topScorers}
        <br>
        <br>
        Overall \{topScorerName} scored the highest. This is why they were picked:
        <br>
        <br>
        \{gptResponse}
        </p>
        """;
        model.addAttribute("justification", justification);
        model.addAttribute("buttonText", STR."Download " + topScorerName + "s CV");
        model.addAttribute("id", topMatches.getFirst().cvDownload());
        return "gptchat";
    }
}
