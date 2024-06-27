package org.smartjobs.adaptors.view.web.controller.candidate;

import display.CamelCaseDisplayNameGenerator;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.smartjobs.core.ports.dal.CvDal;
import org.smartjobs.core.service.CandidateService;
import org.smartjobs.core.service.candidate.CandidateServiceImpl;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static constants.TestConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@DisplayNameGeneration(CamelCaseDisplayNameGenerator.class)
class CandidateControllerTest {


    private final CandidateController candidateController = new CandidateController(CANDIDATE_SERVICE, ROLE_SERVICE);

    @Test
    void testUploadFileReturnsTheCandidatePageWithTheCorrectInformation() {
        String candidatePage = candidateController.uploadFile(USER, new MultipartFile[]{fileTxt()}, MODEL);
        assertEquals(SELECTED_CANDIDATE_COUNT, MODEL.getAttribute("selectedCount"));
        assertEquals(POSITION, MODEL.getAttribute("currentRole"));
        assertEquals("candidate/candidates", candidatePage);
    }

    @Test
    void testGetAllCandidatesReturnsAllTheCandidates() {
        String candidateTableFragment = candidateController.getAllCandidates(USER, MODEL);
        assertEquals(List.of(CANDIDATE_DATA2, CANDIDATE_DATA), MODEL.getAttribute("candidates"));
        assertEquals("candidate/table", candidateTableFragment);
    }

    @Test
    void testDeleteCandidateUpdatesTheCandidateCount() {
        MockHttpServletResponse response = mockHttpServletResponse();
        String candidateCountUpdated = candidateController.deleteCandidate(USER, CANDIDATE_ID, response);
        assertEquals("candidate-count-updated", response.getHeader("HX-Trigger"));
        assertEquals("candidate/empty-response", candidateCountUpdated);
    }

    @Test
    void testDeleteAllCandidatesDeletesAllCandidates() {
        MockHttpServletResponse response = mockHttpServletResponse();
        String candidateCountUpdated = candidateController.deleteAllCandidates(USER, MODEL, response);
        assertEquals(Collections.emptyList(), MODEL.getAttribute("candidates"));
        assertEquals("candidate-count-updated", response.getHeader("HX-Trigger"));
        assertEquals("candidate/table", candidateCountUpdated);
    }

    @Test
    void testSelectCandidateReturnsTheCorrectCandidateInformation() {
        MockHttpServletResponse response = mockHttpServletResponse();
        String singleCandidateRowFragment = candidateController.selectCandidate(USER, CV_ID, true, MODEL, response);
        assertEquals(CANDIDATE_DATA, MODEL.getAttribute("candidate"));
        assertEquals("candidate-count-updated", response.getHeader("HX-Trigger"));
        assertEquals("candidate/single-candidate-row", singleCandidateRowFragment);
    }

    @Test
    void testSelectAllCandidatesReturnsTheCorrectCandidateInformation() {
        MockHttpServletResponse response = mockHttpServletResponse();
        String singleCandidateRowFragment = candidateController.selectAllCandidates(USER, true, MODEL, response);
        assertEquals(List.of(CANDIDATE_DATA2, CANDIDATE_DATA), MODEL.getAttribute("candidates"));
        assertEquals("candidate-count-updated", response.getHeader("HX-Trigger"));
        assertEquals("candidate/table", singleCandidateRowFragment);
    }

    @Test
    void testSelectCandidateReturnsTheCorrectCandidateInformationWhenTheCandidateCanNotBeFound() {
        CvDal cvDal = cvDalMock();
        when(cvDal.updateCurrentlySelectedById(CV_ID, true)).thenReturn(Optional.empty());
        CandidateService candidateService = new CandidateServiceImpl(AI_SERVICE, cvDal, EVENT_EMITTER, CREDIT_SERVICE, FILE_HANDLER);
        CandidateController candidateController = new CandidateController(candidateService, ROLE_SERVICE);
        MockHttpServletResponse response = mockHttpServletResponse();
        String emptyResponse = candidateController.selectCandidate(USER, CV_ID, true, MODEL, response);
        assertEquals("candidate-count-updated", response.getHeader("HX-Trigger"));
        assertEquals("candidate/empty-response", emptyResponse);
    }

    @Test
    void testFindNumberOfCandidatesSelectedReturnsTheCorrectNumberOfCandidatesWhenFound() {
        String numberOfCandidatesSelected = candidateController.findNumberOfCandidatesSelected(USER, MODEL);
        assertEquals(SELECTED_CANDIDATE_COUNT, MODEL.getAttribute("selectedCount"));
        assertEquals(POSITION, MODEL.getAttribute("currentRole"));
        assertEquals("candidate/candidate-count", numberOfCandidatesSelected);
    }

    @Test
    void testFindNumberOfCandidatesSelectedReturnsTheCorrectNumberOfCandidatesWhenNotFound() {
        String numberOfCandidatesSelected = candidateController.findNumberOfCandidatesSelected(USER2, MODEL);
        assertEquals(0, MODEL.getAttribute("selectedCount"));
        assertEquals("NONE", MODEL.getAttribute("currentRole"));
        assertEquals("candidate/candidate-count", numberOfCandidatesSelected);
    }
}