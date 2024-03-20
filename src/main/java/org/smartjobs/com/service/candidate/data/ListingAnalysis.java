package org.smartjobs.com.service.candidate.data;

import java.util.List;

public record ListingAnalysis(List<TopMatch> topScorers, String gptJustification, String topScorerName,
                              String topScorerCv, int numberTop) {
}
