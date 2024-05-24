package org.smartjobs.core.service.analysis;

import io.vavr.control.Either;
import org.smartjobs.core.entities.CandidateScores;
import org.smartjobs.core.entities.ProcessedCv;
import org.smartjobs.core.entities.ScoredCriteria;
import org.smartjobs.core.entities.ScoringCriteria;
import org.smartjobs.core.exception.categories.UserResolvedExceptions;
import org.smartjobs.core.exception.categories.UserResolvedExceptions.RoleCriteriaLimitReachedException;
import org.smartjobs.core.exception.categories.UserResolvedExceptions.RoleHasNoCriteriaException;
import org.smartjobs.core.failures.ProcessFailure;
import org.smartjobs.core.ports.client.AiService;
import org.smartjobs.core.ports.dal.AnalysisDal;
import org.smartjobs.core.service.AnalysisService;
import org.smartjobs.core.service.CreditService;
import org.smartjobs.core.service.EventService;
import org.smartjobs.core.service.event.events.ErrorEvent;
import org.smartjobs.core.service.event.events.ProgressEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.smartjobs.core.failures.ProcessFailure.LLM_FAILURE_ANALYZING;
import static org.smartjobs.core.utils.ConcurrencyUtil.virtualThreadListMap;

@Service
public class AnalysisServiceImpl implements AnalysisService {

    private final AiService client;
    private final EventService eventService;
    private final CreditService creditService;
    private final AnalysisDal analysisDal;
    private final int maxRoleCriteriaCount;


    @Autowired
    public AnalysisServiceImpl(AiService client, EventService eventService, CreditService creditService, AnalysisDal analysisDal, @Value("${role.max.criteria}") int maxRoleCriteriaCount) {
        this.client = client;
        this.eventService = eventService;
        this.creditService = creditService;
        this.analysisDal = analysisDal;
        this.maxRoleCriteriaCount = maxRoleCriteriaCount;
    }

    @Override
    public List<CandidateScores> scoreToCriteria(long userId, long roleId, List<ProcessedCv> candidateInformation, List<ScoringCriteria> scoringCriteria) {
        if (candidateInformation.isEmpty()) {
            throw new UserResolvedExceptions.NoCandidatesSelectedException(userId);
        }
        if (scoringCriteria.isEmpty()) {
            throw new RoleHasNoCriteriaException(userId);
        }
        if (scoringCriteria.size() > maxRoleCriteriaCount) {
            throw new RoleCriteriaLimitReachedException(userId, maxRoleCriteriaCount);
        }
        creditService.debit(userId, candidateInformation.size());
        var counter = new AtomicInteger(0);
        var total = candidateInformation.size();
        var results = virtualThreadListMap(candidateInformation, cv -> {
            Either<ProcessFailure, CandidateScores> candidateScores = generateCandidateScore(userId, roleId, cv, scoringCriteria);
            eventService.sendEvent(new ProgressEvent(userId, counter.incrementAndGet(), total));
            return candidateScores;
        });
        List<CandidateScores> passes = results.stream().filter(Either::isRight).map(Either::get).toList();
        List<ProcessFailure> failures = results.stream().filter(Either::isLeft).map(Either::getLeft).toList();
        if (!failures.isEmpty()) {
            creditService.refund(userId, failures.size());
            eventService.sendEvent(new ErrorEvent(userId, failures));
        }
        return passes;
    }

    @Override
    public CandidateScores getResultById(long id) {
        return analysisDal.getResultById(id);
    }

    private Either<ProcessFailure, CandidateScores> generateCandidateScore(long userId, long roleId, ProcessedCv cv, List<ScoringCriteria> scoringCriteria) {
        var results = virtualThreadListMap(scoringCriteria, criteria -> scoreForCriteria(cv, criteria));
        if (results.stream().anyMatch(Optional::isEmpty)) {
            return Either.left(LLM_FAILURE_ANALYZING);
        }
        var clearResults = results.stream().filter(Optional::isPresent).map(Optional::get).toList();
        long analysisId = analysisDal.saveResults(userId, cv.id(), roleId, clearResults);
        return Either.right(new CandidateScores(analysisId, cv.name(), clearResults));
    }

    private Optional<ScoredCriteria> scoreForCriteria(ProcessedCv cv, ScoringCriteria criteria) {
        return client.scoreForCriteria(cv.condensedDescription(), criteria.scoringGuide(), criteria.weighting())
                .map(score -> new ScoredCriteria(criteria.id(), criteria.name(), score.justification(), score.score(), criteria.weighting()));
    }
}
