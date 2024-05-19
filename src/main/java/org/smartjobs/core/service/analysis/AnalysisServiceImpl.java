package org.smartjobs.core.service.analysis;

import org.smartjobs.core.entities.*;
import org.smartjobs.core.ports.client.AiService;
import org.smartjobs.core.service.AnalysisService;
import org.smartjobs.core.service.SseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.smartjobs.core.utils.ConcurrencyUtil.virtualThreadList;

@Service
public class AnalysisServiceImpl implements AnalysisService {

    private final AiService client;
    private final SseService sseService;


    @Autowired
    public AnalysisServiceImpl(AiService client, SseService sseService) {
        this.client = client;
        this.sseService = sseService;
    }

    @Override
    public List<CandidateScores> scoreToCriteria(long userId, List<ProcessedCv> candidateInformation, Role role) {
        var counter = new AtomicInteger(0);
        var total = candidateInformation.size();
        return virtualThreadList(candidateInformation, cv -> {
            CandidateScores candidateScores = generateCandidateScore(cv, role.scoringCriteria());
            sseService.send(userId, "progress-analysis", STR. "<div>Analyzed: \{ counter.incrementAndGet() }/\{ total }</div>" );
            return candidateScores;
        });
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
