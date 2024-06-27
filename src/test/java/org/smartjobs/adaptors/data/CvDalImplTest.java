package org.smartjobs.adaptors.data;

import display.CamelCaseDisplayNameGenerator;
import jakarta.persistence.Tuple;
import jakarta.persistence.TupleElement;
import org.hibernate.sql.results.internal.TupleImpl;
import org.hibernate.sql.results.internal.TupleMetadata;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.smartjobs.adaptors.data.repository.CandidateRepository;
import org.smartjobs.adaptors.data.repository.CvRepository;
import org.smartjobs.adaptors.data.repository.data.Candidate;
import org.smartjobs.adaptors.data.repository.data.Cv;
import org.smartjobs.core.config.DateSupplier;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static constants.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayNameGeneration(CamelCaseDisplayNameGenerator.class)
class CvDalImplTest {

    public static final Candidate CANDIDATE = new Candidate(CANDIDATE_ID, true, CANDIDATE_NAME, CV_ID, USER_ID, ROLE_ID, NOW);
    public static final List<Cv> CV_LIST = List.of(new Cv(CV_ID, HASH_TXT, CV_STRING_CONDENSED));
    public static final Candidate CANDIDATE2 = new Candidate(CANDIDATE_ID2, true, CANDIDATE_NAME2, CV_ID, USER_ID, ROLE_ID, NOW);
    public static final List<Candidate> CANDIDATE_LIST = List.of(CANDIDATE, CANDIDATE2);
    private final CvRepository cvRepository = mock(CvRepository.class);
    private final CandidateRepository candidateRepository = mock(CandidateRepository.class);
    private final DateSupplier dateSupplier = mock(DateSupplier.class);
    private final CvDalImpl cvDal = new CvDalImpl(cvRepository, candidateRepository, dateSupplier);
    private final ArgumentCaptor<Candidate> candidateArgumentCaptor = ArgumentCaptor.forClass(Candidate.class);
    private final ArgumentCaptor<Cv> cvArgumentCaptor = ArgumentCaptor.forClass(Cv.class);

    @Test
    void testAddingCvsToRepositoryWillAddAllProcessedCvsCorrectlyWhenTheFileAlreadyExistsByHash() {
        when(dateSupplier.getDate()).thenReturn(NOW);
        when(cvRepository.findByFileHash(HASH_TXT)).thenReturn(CV_LIST);
        when(candidateRepository.save(candidateArgumentCaptor.capture())).thenAnswer(_ -> candidateArgumentCaptor.getValue());
        cvDal.addCvsToRepository(USER_ID, ROLE_ID, PROCESSED_CV_LIST);
        verify(cvRepository, never()).save(any(Cv.class));

        Candidate savedCandidate = candidateArgumentCaptor.getValue();
        assertEquals(CANDIDATE_NAME, savedCandidate.getName());
        assertEquals(CV_ID, savedCandidate.getCvId());
        assertEquals(NOW, savedCandidate.getLastAccessed());
        assertEquals(USER_ID, savedCandidate.getUserId());
        assertEquals(ROLE_ID, savedCandidate.getRoleId());
        assertEquals(PROCESSED_CV.currentlySelected(), savedCandidate.getCurrentlySelected());
    }

    @Test
    void testAddingCvsToRepositoryWillAddAllProcessedCvsCorrectlyWhenTheFileDoesNotExistByHash() {
        //Given
        when(dateSupplier.getDate()).thenReturn(NOW);
        when(cvRepository.findByFileHash(HASH_TXT)).thenReturn(Collections.emptyList());
        when(candidateRepository.save(candidateArgumentCaptor.capture())).thenAnswer(_ -> candidateArgumentCaptor.getValue());
        when(cvRepository.save(cvArgumentCaptor.capture())).thenReturn(new Cv(CV_ID, "", ""));
        //When
        cvDal.addCvsToRepository(USER_ID, ROLE_ID, PROCESSED_CV_LIST);
        //Then
        Candidate savedCandidate = candidateArgumentCaptor.getValue();
        assertEquals(CANDIDATE_NAME, savedCandidate.getName());
        assertEquals(CV_ID, savedCandidate.getCvId());
        assertEquals(NOW, savedCandidate.getLastAccessed());
        assertEquals(USER_ID, savedCandidate.getUserId());
        assertEquals(ROLE_ID, savedCandidate.getRoleId());
        assertEquals(PROCESSED_CV.currentlySelected(), savedCandidate.getCurrentlySelected());
        Cv cv = cvArgumentCaptor.getValue();
        assertEquals(HASH_TXT, cv.getFileHash());
        assertEquals(CV_STRING_CONDENSED, cv.getCondensedText());
    }

    @Test
    void testThatGetAllCandidatesReturnsTheCorrectCandidates(){
        when(candidateRepository.findAllByUserIdAndRoleId(USER_ID, ROLE_ID)).thenReturn(CANDIDATE_LIST);
        assertEquals(CANDIDATE_DATA_LIST, cvDal.getAllCandidates(USER_ID, ROLE_ID));
    }

    @Test
    void testGetAllSelectedReturnsTheCorrectValues(){
        Tuple tuple = new TupleImpl(
                new TupleMetadata(new TupleElement[]{}, new String[]{"id", "name", "file_hash", "condensed_text"}),
                new Object[]{CV_ID, CANDIDATE_NAME, HASH_TXT, CV_STRING_CONDENSED});
        Tuple tuple2 = new TupleImpl(
                new TupleMetadata(new TupleElement[]{}, new String[]{"id", "name", "file_hash", "condensed_text"}),
                new Object[]{CV_ID2, CANDIDATE_NAME2, HASH_TXT, CV_STRING_CONDENSED2});
        when(cvRepository.findByCurrentlySelected(true, USER_ID, ROLE_ID)).thenReturn(List.of(tuple2, tuple));
        assertEquals(PROCESSED_CV_LIST, cvDal.getAllSelected(USER_ID, ROLE_ID));
    }

    @Test
    void testDeleteByCandidateIdDeletesByTheGivenId(){
        cvDal.deleteByCandidateId(CANDIDATE_ID);
        verify(candidateRepository).deleteById(CANDIDATE_ID);
    }

    @Test
    void testKnownHashReturnsTrueWhenTheHashIsKnown(){
        when(cvRepository.existsCvByFileHash(HASH_TXT)).thenReturn(true);
        assertTrue(cvDal.knownHash(HASH_TXT));
    }

    @Test
    void testKnownHashReturnsFalseWhenTheHashIsNotKnown(){
        when(cvRepository.existsCvByFileHash(HASH_TXT)).thenReturn(false);
        assertFalse(cvDal.knownHash(HASH_TXT));
    }

    @Test
    void testUpdateCurrentlySelectedByIdReturnsTheUpdatedValue(){
        when(candidateRepository.updateCurrentlySelectedById(CV_ID, true)).thenReturn(Optional.of(CANDIDATE));
        assertEquals(Optional.of(CANDIDATE_DATA), cvDal.updateCurrentlySelectedById(CV_ID, true));
    }

    @Test
    void testUpdateCurrentlySelectedByIdReturnsEmptyOptionalWhenCandidateNotFound(){
        when(candidateRepository.updateCurrentlySelectedById(CV_ID, true)).thenReturn(Optional.empty());
        assertEquals(Optional.empty(), cvDal.updateCurrentlySelectedById(CV_ID, true));
    }

    @Test
    void testUpdateCurrentlySelectedAllReturnsTheUpdatedValue() {
        when(candidateRepository.updateCurrentlySelectedAll(USER_ID, ROLE_ID, true)).thenReturn(CANDIDATE_LIST);
        assertEquals(CANDIDATE_DATA_LIST, cvDal.updateCurrentlySelectedAll(USER_ID, ROLE_ID, true));
    }

    @Test
    void testThatFindBySelectedCandidateCountReturnsTheCurrentNumberOfSelectedCandidates(){
        when(candidateRepository.countByCurrentlySelectedAndUserIdAndRoleId(true, USER_ID, ROLE_ID)).thenReturn(SELECTED_CANDIDATE_COUNT);
        assertEquals(SELECTED_CANDIDATE_COUNT, cvDal.findSelectedCandidateCount(USER_ID, ROLE_ID));
    }

    @Test
    void testThatDeleteAllCandidatesWillDeleteAllByUserIdAndRoleId(){
        cvDal.deleteAllCandidates(USER_ID, ROLE_ID);
        verify(candidateRepository).deleteByUserIdAndRoleId(USER_ID, ROLE_ID);
    }

    @Test
    void testThatGetByHashReturnsValidCv(){
        when(cvRepository.findByFileHash(HASH_TXT)).thenReturn(CV_LIST);
        assertEquals(Optional.of(CV_DATA), cvDal.getByHash(HASH_TXT));
    }

    @Test
    void testThatGetByCvIdReturnsTheCorrectCandidate(){
        when(candidateRepository.findAllByCvId(CV_ID)).thenReturn(List.of(CANDIDATE, CANDIDATE2));
        assertEquals(CANDIDATE_DATA_LIST, cvDal.getByCvId(CV_ID));
    }
}
