package org.smartjobs.core.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.smartjobs.core.entities.CandidateData;
import org.smartjobs.core.entities.ProcessedCv;
import org.smartjobs.core.ports.dal.CvDal;
import org.smartjobs.core.service.candidate.CandidateServiceImpl;

import java.util.List;
import java.util.Optional;

import static constants.TestConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

class CandidateServiceTest {

    private CandidateService candidateService;
    private CvDal cvDal;

    @BeforeEach
    void setUp() {
        cvDal = cvDalMock();
        candidateService = new CandidateServiceImpl(aiServiceMock(), cvDal, eventEmitter(), creditService(), fileHandler());
    }

    @Test
    void testGetFullCandidateInfoReturnsCorrectInformation() {
        List<ProcessedCv> fullCandidateInfo = candidateService.getFullCandidateInfo(USER_ID, ROLE_ID);
        assertEquals(PROCESSED_CV_LIST, fullCandidateInfo);
    }

    @Test
    void getCurrentCandidates() {
        List<CandidateData> currentCandidates = candidateService.getCurrentCandidates(USER_ID, ROLE_ID);
        assertEquals(CANDIDATE_DATA_LIST, currentCandidates);
    }

    @Test
    void testUpdateCandidateCvsReturnsTheUpdatedCandidateCvs() {
        List<ProcessedCv> processedCvs = candidateService.updateCandidateCvs(USER_ID, ROLE_ID, List.of(file()));
        assertEquals(List.of(new ProcessedCv(null, CANDIDATE_NAME, true, HASH, CV_STRING_CONDENSED)), processedCvs);
    }

    @Test
    void testDeleteCandidateDeletesTheCandidateFromThePropagationLayer() {
        candidateService.deleteCandidate(USER_ID, ROLE_ID, CANDIDATE_ID);
        verify(cvDal).deleteByCandidateId(CANDIDATE_ID);
    }

    @Test
    void testToggleCandidateSelectSelecting() {
        Optional<CandidateData> candidateData = candidateService.toggleCandidateSelect(USER_ID, ROLE_ID, CV_ID, true);
        assertEquals(Optional.of(CANDIDATE_DATA), candidateData);
    }

    @Test
    void testToggleCandidateSelectUnselecting() {
        Optional<CandidateData> candidateData = candidateService.toggleCandidateSelect(USER_ID, ROLE_ID, CV_ID, false);
        assertEquals(Optional.of(CANDIDATE_DATA_UNSELECTED), candidateData);
    }

    @Test
    void testFindSelectedCandidateCountReturnsTheCorrectCandidateCount() {
        int selectedCandidateCount = candidateService.findSelectedCandidateCount(USER_ID, ROLE_ID);
        assertEquals(SELECTED_CANDIDATE_COUNT, selectedCandidateCount);

    }

    @Test
    void testDeleteAllCandidatesDeletesAllCandidatesForTheRoleAndTheUserFromThePersistenceLayer() {
        candidateService.deleteAllCandidates(USER_ID, ROLE_ID);
        verify(cvDal).deleteAllCandidates(USER_ID, ROLE_ID);
    }
}