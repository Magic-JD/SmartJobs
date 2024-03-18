package org.smartjobs.com.controller.listing.response;

import java.util.List;

public record MatchResponse(List<TopMatch> topMatches, String justification, String fileLocation) {
}

