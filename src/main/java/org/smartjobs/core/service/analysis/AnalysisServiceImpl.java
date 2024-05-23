package org.smartjobs.core.service.analysis;

import org.smartjobs.core.entities.*;
import org.smartjobs.core.exception.categories.UserResolvedExceptions.RoleCriteriaLimitReachedException;
import org.smartjobs.core.exception.categories.UserResolvedExceptions.RoleHasNoCriteriaException;
import org.smartjobs.core.ports.client.AiService;
import org.smartjobs.core.service.AnalysisService;
import org.smartjobs.core.service.SseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.smartjobs.core.utils.ConcurrencyUtil.virtualThreadList;

@Service
public class AnalysisServiceImpl implements AnalysisService {

    private final AiService client;
    private final SseService sseService;
    private final int maxRoleCriteriaCount;


    @Autowired
    public AnalysisServiceImpl(AiService client, SseService sseService, @Value("${role.max.criteria}") int maxRoleCriteriaCount) {
        this.client = client;
        this.sseService = sseService;
        this.maxRoleCriteriaCount = maxRoleCriteriaCount;
    }

    @Override
    public List<CandidateScores> scoreToCriteria(long userId, List<ProcessedCv> candidateInformation, Role role) {
        if (role.scoringCriteria().isEmpty()) {
            throw new RoleHasNoCriteriaException();
        }
        if (role.scoringCriteria().size() > maxRoleCriteriaCount) {
            throw new RoleCriteriaLimitReachedException(maxRoleCriteriaCount);
        }
        var counter = new AtomicInteger(0);
        var total = candidateInformation.size();
        return virtualThreadList(candidateInformation, cv -> {
            Optional<CandidateScores> candidateScores = generateCandidateScore(cv, role.scoringCriteria());
            sseService.send(userId, "progress-analysis", STR. "<div>Analyzed: \{ counter.incrementAndGet() }/\{ total }</div>" );
            return candidateScores;
        }).stream().filter(Optional::isPresent).map(Optional::get).toList();
    }

    private Optional<CandidateScores> generateCandidateScore(ProcessedCv cv, List<ScoringCriteria> scoringCriteria) {
        var results = virtualThreadList(scoringCriteria, criteria -> scoreForCriteria(cv, criteria));
        if (results.stream().anyMatch(Optional::isEmpty)) {
            return Optional.empty();
        }
        var clearResults = results.stream().filter(Optional::isPresent).map(Optional::get).toList();
        double totalPossibleScore = scoringCriteria.stream()
                .mapToInt(ScoringCriteria::weighting).sum();
        double achievedScore = clearResults.stream()
                .mapToDouble(ScoredCriteria::score).sum();
        double percentage = (achievedScore / totalPossibleScore) * 100;
        return Optional.of(new CandidateScores(UUID.randomUUID().toString(), cv.name(), percentage, clearResults));
    }

    private Optional<ScoredCriteria> scoreForCriteria(ProcessedCv cv, ScoringCriteria criteria) {
        return client.scoreForCriteria(cv.condensedDescription(), criteria.scoringGuide(), criteria.weighting())
                .map(score -> new ScoredCriteria(criteria.name(), score.justification(), score.score(), criteria.weighting()));
    }


}
