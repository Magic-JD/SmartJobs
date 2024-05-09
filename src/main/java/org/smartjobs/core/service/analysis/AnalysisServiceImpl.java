package org.smartjobs.core.service.analysis;

import org.smartjobs.core.entities.*;
import org.smartjobs.core.ports.client.AiClient;
import org.smartjobs.core.service.AnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static org.smartjobs.core.utils.ConcurrencyUtil.virtualThreadList;

@Service
public class AnalysisServiceImpl implements AnalysisService {

    private final AiClient client;


    @Autowired
    public AnalysisServiceImpl(AiClient client) {
        this.client = client;
    }

    @Override
    public List<CandidateScores> scoreToCriteria(List<ProcessedCv> candidateInformation, Role role) {
        return virtualThreadList(candidateInformation, cv -> generateCandidateScore(cv, role.scoringCriteria()));
    }

    private CandidateScores generateCandidateScore(ProcessedCv cv, List<ScoringCriteria> scoringCriteria) {
        var results = virtualThreadList(scoringCriteria, criteria -> scoreForCriteria(cv, criteria));
        double totalPossibleScore = scoringCriteria.stream()
                .mapToInt(ScoringCriteria::weighting).sum();
        double achievedScore = results.stream()
                .mapToDouble(ScoredCriteria::score).sum();
        double percentage = (achievedScore / totalPossibleScore) * 100;
        return new CandidateScores(UUID.randomUUID().toString(), cv.name(), percentage, results);
    }

    private ScoredCriteria scoreForCriteria(ProcessedCv cv, ScoringCriteria criteria) {
        return client.scoreForCriteria(cv.condensedDescription(), criteria.scoringGuide(), criteria.weighting())
                .map(score -> new ScoredCriteria(criteria.name(), score.justification(), score.score(), criteria.weighting()))
                .orElse(new ScoredCriteria(criteria.name(), "The score could not be calculated for this value", 0, 0));
    }


}
