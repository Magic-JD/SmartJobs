package org.smartjobs.core.service;

import org.junit.jupiter.api.Test;
import org.smartjobs.SmartJobs;
import org.smartjobs.core.config.CoreFilter;
import org.smartjobs.core.config.MockPortConfig;
import org.smartjobs.core.entities.CandidateScores;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.filter.TypeExcludeFilters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.smartjobs.core.config.MockPortConfig.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = SmartJobs.class)
@AutoConfigureMockMvc
@ContextConfiguration(classes = MockPortConfig.class)
@TestPropertySource(locations = "classpath:application-test.yml")
@TypeExcludeFilters(CoreFilter.class)
class AnalysisServiceTest {

    @Autowired
    AnalysisService analysisService;

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