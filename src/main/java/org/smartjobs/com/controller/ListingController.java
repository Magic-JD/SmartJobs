package org.smartjobs.com.controller;

import jakarta.validation.Valid;
import org.smartjobs.com.controller.request.MatchRequest;
import org.smartjobs.com.controller.response.MatchResponse;
import org.smartjobs.com.service.CandidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/listing")
public class ListingController {

    @Autowired
    private CandidateService service;

    @PostMapping("/match")
    @ResponseBody
    public ResponseEntity<MatchResponse> evaluateCandidate(@Valid @RequestBody MatchRequest request) {
        return ResponseEntity.ok(
                new MatchResponse(service.findBestMatchForListing(request.listing()))
        );
    }
}
