package org.smartjobs.com.controller.listing;

import jakarta.validation.Valid;
import org.smartjobs.com.controller.listing.request.MatchRequest;
import org.smartjobs.com.controller.listing.response.MatchResponse;
import org.smartjobs.com.controller.listing.response.TopMatch;
import org.smartjobs.com.service.candidate.CandidateService;
import org.smartjobs.com.service.candidate.data.JobMatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


@Component
public class ListingController {

    @Autowired
    private CandidateService service;

    public ResponseEntity<MatchResponse> evaluateCandidate(@Valid @RequestBody MatchRequest request) {
        List<JobMatch> bestMatches = service.findBestMatchForListing(request.listing());
        JobMatch bestMatch = bestMatches.getFirst();
        String justification = service.justifyDecision(bestMatch.match(), bestMatch.fullText(), request.listing());
        List<TopMatch> topFive = bestMatches.stream().map(jm -> new TopMatch(jm.name(), jm.match(), jm.cvDownload())).toList();
        return ResponseEntity.ok(
                new MatchResponse(topFive, justification, bestMatch.cvDownload())
        );
    }
}
