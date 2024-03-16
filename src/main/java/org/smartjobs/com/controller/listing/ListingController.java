package org.smartjobs.com.controller.listing;

import jakarta.validation.Valid;
import org.smartjobs.com.controller.listing.request.MatchRequest;
import org.smartjobs.com.controller.listing.response.MatchResponse;
import org.smartjobs.com.service.candidate.CandidateService;
import org.smartjobs.com.service.candidate.data.JobMatch;
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
        JobMatch bestMatchForListing = service.findBestMatchForListing(request.listing());
        String justification = service.justifyDecision(bestMatchForListing.match(), bestMatchForListing.fullText(), request.listing());
        return ResponseEntity.ok(
                new MatchResponse(justification, bestMatchForListing.cvDownload())
        );
    }
}
