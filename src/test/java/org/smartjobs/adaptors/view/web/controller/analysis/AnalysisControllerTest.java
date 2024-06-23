package org.smartjobs.adaptors.view.web.controller.analysis;

import display.CamelCaseDisplayNameGenerator;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;

import java.util.List;

import static constants.TestConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayNameGeneration(CamelCaseDisplayNameGenerator.class)
class AnalysisControllerTest {

    private final AnalysisController analysisController = new AnalysisController(CANDIDATE_SERVICE, ANALYSIS_SERVICE, ROLE_SERVICE);

    @Test
    void testThatScoreAllCandidatesWillAddExpectedResultsInTheCorrectOrder() {
        String fragment = analysisController.scoreAllCandidates(USER, MODEL);
        assertEquals(List.of(CANDIDATE_SCORES, CANDIDATE_SCORES2), MODEL.getAttribute("results"));
        assertEquals("analyze/scoring", fragment);
    }

    @Test
    void testRetrieveResultDetailsReceivesTheCorrectResults() {
        String fragment = analysisController.retrieveResultDetails(ANALYSIS_ID, MODEL);
        assertEquals(CANDIDATE_SCORES, MODEL.getAttribute("result"));
        assertEquals("analyze/result-details", fragment);
    }

    @Test
    void testRemoveResultDetailsReturnsTheEmptyFragmentForTheCorrectResults() {
        String fragment = analysisController.removeResultDetails(ANALYSIS_ID, MODEL);
        assertEquals(CANDIDATE_SCORES, MODEL.getAttribute("result"));
        assertEquals("analyze/result-collapsed", fragment);
    }
}