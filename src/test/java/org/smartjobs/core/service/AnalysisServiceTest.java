package org.smartjobs.core.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.smartjobs.core.entities.CandidateScores;
import org.smartjobs.core.service.analysis.AnalysisServiceImpl;

import java.util.List;

import static constants.TestConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AnalysisServiceTest {

    AnalysisService analysisService;

    @BeforeEach
    void setUp() {
        analysisService = new AnalysisServiceImpl(aiServiceMock(), eventEmitter(), creditService(), analysisDalMock(), 10);
    }

    @Test
    void testScoreToCriteriaReturnsTheCorrectCandidateScores() {
        List<CandidateScores> candidateScores = analysisService.scoreToCriteria(USER_ID, ROLE_ID, PROCESSED_CV_LIST, USER_SCORING_CRITERIA_LIST);
        assertEquals(1, candidateScores.size());
        CandidateScores cs = candidateScores.get(0);
        assertEquals(CANDIDATE_SCORES, cs);
    }

    @Test
    void testGetResultByIdReturnsTheCorrectCandidateScoresForThatId() {
        CandidateScores candidateScores = analysisService.getResultById(ANALYSIS_ID);
        assertEquals(CANDIDATE_SCORES, candidateScores);
    }
}