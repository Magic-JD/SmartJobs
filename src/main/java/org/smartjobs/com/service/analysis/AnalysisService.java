package org.smartjobs.com.service.analysis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartjobs.com.client.gpt.GptClient;
import org.smartjobs.com.service.candidate.data.JobMatch;
import org.smartjobs.com.service.candidate.data.ListingAnalysis;
import org.smartjobs.com.service.candidate.data.ProcessedCv;
import org.smartjobs.com.service.candidate.data.TopMatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.smartjobs.com.concurrency.ConcurrencyUtil.virtualThreadList;

@Service
public class AnalysisService {

    private static final Logger logger = LoggerFactory.getLogger(AnalysisService.class);

    private final GptClient client;
    private final int maxTop;


    @Autowired
    public AnalysisService(GptClient client, @Value("${candidate.max.top}") int maxTop) {
        this.client = client;
        this.maxTop = maxTop;
    }

    public Optional<ListingAnalysis> analyseListing(String listing, List<ProcessedCv> allCvs) {
        if (allCvs.isEmpty()) {
            logger.info("There are no candidates to assess.");
            return empty();
        }
        List<JobMatch> bestMatches = allCvs.parallelStream()
                .map(cv -> jobMatch(listing, cv))
                .sorted(Comparator.comparing(JobMatch::match, Comparator.reverseOrder()))
                .limit(maxTop).toList();
        JobMatch bestMatch = bestMatches.getFirst();
        String gptJustification = client.justifyDecision(bestMatch.match(), bestMatch.fullText(), listing);
        List<TopMatch> topMatches = bestMatches.stream().map(jm -> new TopMatch(jm.name(), jm.match(), jm.cvDownload())).toList();
        String topScorerName = topMatches.getFirst().name();
        return of(new ListingAnalysis(topMatches, gptJustification, topScorerName, bestMatch.cvDownload(), topMatches.size()));
    }

    private JobMatch jobMatch(String listingDescription, ProcessedCv cv) {
        int matchPercentage = client.determineMatchPercentage(listingDescription, cv.condensedDescription());
        logger.debug("CV ID:{} NAME:{} given a match percentage of {}%", cv.fileLocation(), cv.name(), matchPercentage);
        return new JobMatch(cv.name(), matchPercentage, cv.fullDescription(), cv.fileLocation());
    }

    public List<GptClient.ScoringCriteriaResult> scoreToCriteria(List<ProcessedCv> candidateInformation) {
        return virtualThreadList(candidateInformation, client::scoreToCriteria);
    }


}
