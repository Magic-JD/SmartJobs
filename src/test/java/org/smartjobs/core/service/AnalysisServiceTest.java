package org.smartjobs.core.service;

import display.CamelCaseDisplayNameGenerator;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.smartjobs.core.entities.CandidateScores;
import org.smartjobs.core.entities.ProcessedCv;
import org.smartjobs.core.entities.UserScoringCriteria;
import org.smartjobs.core.exception.categories.UserResolvedExceptions.NoCandidatesSelectedException;
import org.smartjobs.core.exception.categories.UserResolvedExceptions.RoleCriteriaLimitReachedException;
import org.smartjobs.core.exception.categories.UserResolvedExceptions.RoleHasNoCriteriaException;

import java.util.Collections;
import java.util.List;

import static constants.TestConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayNameGeneration(CamelCaseDisplayNameGenerator.class)
class AnalysisServiceTest {

    public static final List<ProcessedCv> CANDIDATE_INFORMATION_EMPTY_LIST = Collections.emptyList();
    public static final List<UserScoringCriteria> USER_SCORING_CRITERIA_EMPTY_LIST = Collections.emptyList();
    public static final UserScoringCriteria DUMMY_USER_SCORING_CRITERIA = new UserScoringCriteria(0, null, null, false, 0, null);
    public static final List<UserScoringCriteria> USER_SCORING_CRITERIA_EXTENDED_LIST = List.of(
            DUMMY_USER_SCORING_CRITERIA,
            DUMMY_USER_SCORING_CRITERIA,
            DUMMY_USER_SCORING_CRITERIA,
            DUMMY_USER_SCORING_CRITERIA,
            DUMMY_USER_SCORING_CRITERIA,
            DUMMY_USER_SCORING_CRITERIA,
            DUMMY_USER_SCORING_CRITERIA
    );

    @Test
    void testScoreToCriteriaReturnsTheCorrectCandidateScores() {
        List<CandidateScores> candidateScores = ANALYSIS_SERVICE.scoreToCriteria(USER_ID, ROLE_ID, PROCESSED_CV_LIST, USER_SCORING_CRITERIA_LIST);
        assertEquals(2, candidateScores.size());
        CandidateScores cs2 = candidateScores.get(0);
        CandidateScores cs = candidateScores.get(1);
        assertEquals(CANDIDATE_SCORES2, cs2);
        assertEquals(CANDIDATE_SCORES, cs);
    }

    @Test
    void testScoreToCriteriaThrowsNoCandidateSelectedExceptionIfThereAreNoCandidatesGiven() {
        assertThrows(NoCandidatesSelectedException.class, () -> ANALYSIS_SERVICE.scoreToCriteria(USER_ID, ROLE_ID, CANDIDATE_INFORMATION_EMPTY_LIST, USER_SCORING_CRITERIA_LIST));
    }

    @Test
    void testScoreToCriteriaThrowsRoleHasNoCriteriaExceptionIfThereAreNoCriteriaGiven() {
        assertThrows(RoleHasNoCriteriaException.class, () -> ANALYSIS_SERVICE.scoreToCriteria(USER_ID, ROLE_ID, PROCESSED_CV_LIST, USER_SCORING_CRITERIA_EMPTY_LIST));
    }

    @Test
    void testScoreToCriteriaThrowsRoleCriteriaLimitReachedExceptionIfThereAreTooManyCriteriaGiven() {
        assertThrows(RoleCriteriaLimitReachedException.class, () -> ANALYSIS_SERVICE.scoreToCriteria(USER_ID, ROLE_ID, PROCESSED_CV_LIST, USER_SCORING_CRITERIA_EXTENDED_LIST));
    }

    @Test
    void testScoreToCriteriaOnlyReturnsTheCandidateScoresThatPass() {
        List<CandidateScores> candidateScores = ANALYSIS_SERVICE.scoreToCriteria(USER_ID, ROLE_ID, List.of(PROCESSED_CV, PROCESSED_CV_INVALID), USER_SCORING_CRITERIA_LIST);
        assertEquals(1, candidateScores.size());
        CandidateScores cs = candidateScores.get(0);
        assertEquals(CANDIDATE_SCORES, cs);
    }

    @Test
    void testGetResultByIdReturnsTheCorrectCandidateScoresForThatId() {
        CandidateScores candidateScores = ANALYSIS_SERVICE.getResultById(ANALYSIS_ID);
        assertEquals(CANDIDATE_SCORES, candidateScores);
    }
}
