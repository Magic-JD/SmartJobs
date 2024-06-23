package org.smartjobs.core.service;

import display.CamelCaseDisplayNameGenerator;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.smartjobs.core.entities.CandidateScores;

import java.util.List;

import static constants.TestConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayNameGeneration(CamelCaseDisplayNameGenerator.class)
class AnalysisServiceTest {

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
    void testGetResultByIdReturnsTheCorrectCandidateScoresForThatId() {
        CandidateScores candidateScores = ANALYSIS_SERVICE.getResultById(ANALYSIS_ID);
        assertEquals(CANDIDATE_SCORES, candidateScores);
    }
}
