package org.smartjobs.core.service;

import display.CamelCaseDisplayNameGenerator;
import org.junit.jupiter.api.DisplayNameGeneration;
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

@DisplayNameGeneration(CamelCaseDisplayNameGenerator.class)
class CandidateServiceTest {

    @Test
    void testGetFullCandidateInfoReturnsCorrectInformation() {
        List<ProcessedCv> fullCandidateInfo = CANDIDATE_SERVICE.getFullCandidateInfo(USER_ID, ROLE_ID);
        assertEquals(PROCESSED_CV_LIST, fullCandidateInfo);
    }

    @Test
    void getCurrentCandidates() {
        List<CandidateData> currentCandidates = CANDIDATE_SERVICE.getCurrentCandidates(USER_ID, ROLE_ID);
        assertEquals(CANDIDATE_DATA_LIST, currentCandidates);
    }

    @Test
    void testUpdateCandidateCvsReturnsTheUpdatedCandidateCvs() {
        List<ProcessedCv> processedCvs = CANDIDATE_SERVICE.updateCandidateCvs(USER_ID, ROLE_ID, List.of(fileTxt()));
        assertEquals(List.of(new ProcessedCv(null, CANDIDATE_NAME, true, HASH_TXT, CV_STRING_CONDENSED)), processedCvs);
    }

    @Test
    void testDeleteCandidateDeletesTheCandidateFromThePropagationLayer() {
        CvDal cvDal = cvDalMock();
        CandidateService candidateService = new CandidateServiceImpl(AI_SERVICE, cvDal, EVENT_EMITTER, CREDIT_SERVICE, FILE_HANDLER);
        candidateService.deleteCandidate(USER_ID, ROLE_ID, CANDIDATE_ID);
        verify(cvDal).deleteByCandidateId(CANDIDATE_ID);
    }

    @Test
    void testToggleCandidateSelectSelecting() {
        Optional<CandidateData> candidateData = CANDIDATE_SERVICE.toggleCandidateSelect(USER_ID, ROLE_ID, CV_ID, true);
        assertEquals(Optional.of(CANDIDATE_DATA), candidateData);
    }

    @Test
    void testToggleCandidateAllSelectSelecting() {
        List<CandidateData> candidateData = CANDIDATE_SERVICE.toggleCandidateSelectAll(USER_ID, ROLE_ID, true);
        assertEquals(CANDIDATE_DATA_LIST, candidateData);
    }

    @Test
    void testToggleCandidateSelectUnselecting() {
        Optional<CandidateData> candidateData = CANDIDATE_SERVICE.toggleCandidateSelect(USER_ID, ROLE_ID, CV_ID, false);
        assertEquals(Optional.of(CANDIDATE_DATA_UNSELECTED), candidateData);
    }

    @Test
    void testFindSelectedCandidateCountReturnsTheCorrectCandidateCount() {
        int selectedCandidateCount = CANDIDATE_SERVICE.findSelectedCandidateCount(USER_ID, ROLE_ID);
        assertEquals(SELECTED_CANDIDATE_COUNT, selectedCandidateCount);

    }

    @Test
    void testDeleteAllCandidatesDeletesAllCandidatesForTheRoleAndTheUserFromThePersistenceLayer() {
        CvDal cvDal = cvDalMock();
        CandidateService candidateService = new CandidateServiceImpl(AI_SERVICE, cvDal, EVENT_EMITTER, CREDIT_SERVICE, FILE_HANDLER);
        candidateService.deleteAllCandidates(USER_ID, ROLE_ID);
        verify(cvDal).deleteAllCandidates(USER_ID, ROLE_ID);
    }
}
