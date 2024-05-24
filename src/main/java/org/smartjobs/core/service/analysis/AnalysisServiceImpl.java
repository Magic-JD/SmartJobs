package org.smartjobs.core.service.analysis;

import org.smartjobs.adaptors.view.web.service.SseService;
import org.smartjobs.core.entities.CandidateScores;
import org.smartjobs.core.entities.ProcessedCv;
import org.smartjobs.core.entities.ScoredCriteria;
import org.smartjobs.core.entities.ScoringCriteria;
import org.smartjobs.core.exception.categories.UserResolvedExceptions;
import org.smartjobs.core.exception.categories.UserResolvedExceptions.RoleCriteriaLimitReachedException;
import org.smartjobs.core.exception.categories.UserResolvedExceptions.RoleHasNoCriteriaException;
import org.smartjobs.core.ports.client.AiService;
import org.smartjobs.core.service.AnalysisService;
import org.smartjobs.core.service.CreditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.smartjobs.core.utils.ConcurrencyUtil.virtualThreadListMap;

@Service
public class AnalysisServiceImpl implements AnalysisService {

    private final AiService client;
    private final SseService sseService;
    private final CreditService creditService;
    private final int maxRoleCriteriaCount;


    @Autowired
    public AnalysisServiceImpl(AiService client, SseService sseService, CreditService creditService, @Value("${role.max.criteria}") int maxRoleCriteriaCount) {
        this.client = client;
        this.sseService = sseService;
        this.creditService = creditService;
        this.maxRoleCriteriaCount = maxRoleCriteriaCount;
    }

    @Override
    public List<CandidateScores> scoreToCriteria(long userId, List<ProcessedCv> candidateInformation, List<ScoringCriteria> scoringCriteria) {
        if (candidateInformation.isEmpty()) {
            throw new UserResolvedExceptions.NoCandidatesSelectedException(userId);
        }
        if (scoringCriteria.isEmpty()) {
            throw new RoleHasNoCriteriaException(userId);
        }
        if (scoringCriteria.size() > maxRoleCriteriaCount) {
            throw new RoleCriteriaLimitReachedException(userId, maxRoleCriteriaCount);
        }
        if (!creditService.debitAndVerify(userId, candidateInformation.size())) {
            throw new UserResolvedExceptions.NotEnoughCreditException(userId);
        }
        var counter = new AtomicInteger(0);
        var total = candidateInformation.size();
        List<CandidateScores> results = virtualThreadListMap(candidateInformation, cv -> {
            Optional<CandidateScores> candidateScores = generateCandidateScore(cv, scoringCriteria);
            sseService.send(userId, "progress-analysis", STR. "<div>Analyzed: \{ counter.incrementAndGet() }/\{ total }</div>" );
            return candidateScores;
        }).stream().filter(Optional::isPresent).map(Optional::get).toList();
        var failedCount = candidateInformation.size() - results.size();
        if (failedCount > 0) {
            creditService.refund(userId, failedCount);
        }
        return results;
    }

    private Optional<CandidateScores> generateCandidateScore(ProcessedCv cv, List<ScoringCriteria> scoringCriteria) {
        var results = virtualThreadListMap(scoringCriteria, criteria -> scoreForCriteria(cv, criteria));
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
