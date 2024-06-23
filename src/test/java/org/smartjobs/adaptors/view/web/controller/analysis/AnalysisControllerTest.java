package org.smartjobs.adaptors.view.web.controller.analysis;

import display.CamelCaseDisplayNameGenerator;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.smartjobs.core.service.AnalysisService;
import org.smartjobs.core.service.CandidateService;
import org.smartjobs.core.service.RoleService;
import org.smartjobs.core.service.analysis.AnalysisServiceImpl;
import org.smartjobs.core.service.candidate.CandidateServiceImpl;
import org.smartjobs.core.service.role.RoleServiceImpl;

import java.util.List;

import static constants.TestConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayNameGeneration(CamelCaseDisplayNameGenerator.class)
class AnalysisControllerTest {

    private final AnalysisService analysisService = new AnalysisServiceImpl(aiServiceMock(), eventEmitter(), creditService(), analysisDalMock(), ROLE_CRITERIA_COUNT);
    private final CandidateService candidateService = new CandidateServiceImpl(aiServiceMock(), cvDalMock(), eventEmitter(), creditService(), fileHandler());
    private final RoleService roleService = new RoleServiceImpl(roleDalMock(), ROLE_CRITERIA_COUNT);

    private final AnalysisController analysisController = new AnalysisController(candidateService, analysisService, roleService);

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