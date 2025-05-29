package org.smartjobs.adaptors.data;

import display.CamelCaseDisplayNameGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.smartjobs.adaptors.data.repository.AnalysisRepository;
import org.smartjobs.adaptors.data.repository.CriteriaAnalysisRepository;
import org.smartjobs.adaptors.data.repository.CvRepository;
import org.smartjobs.adaptors.data.repository.RoleRepository;
import org.smartjobs.adaptors.data.repository.data.Analysis;
import org.smartjobs.adaptors.data.repository.data.CriteriaAnalysis;
import org.smartjobs.core.entities.CandidateScores;
import org.smartjobs.core.ports.dal.AnalysisDal;

import java.util.List;

import static constants.TestConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@DisplayNameGeneration(CamelCaseDisplayNameGenerator.class)
class AnalysisDalImplTest {
    public static final long CRITERIA_ANALYSIS_ID = 534543L;
    public static final Analysis ANALYSIS = new Analysis(ANALYSIS_ID, USER_ID, DATABASE_CV, DATABASE_ROLE, null);
    public static final Analysis ANALYSIS2 = new Analysis(ANALYSIS_ID2, USER_ID, DATABASE_CV, DATABASE_ROLE, null);
    public static final List<CriteriaAnalysis> CRITERIA_ANALYSIS_LIST = List.of(
            new CriteriaAnalysis(CRITERIA_ANALYSIS_ID, ANALYSIS2, SCORE_VALUE_GOOD, MAX_SCORE_VALUE, CRITERIA_DESCRIPTION, JUSTIFICATION_POSITIVE, USER_CRITERIA_ID2),
            new CriteriaAnalysis(CRITERIA_ANALYSIS_ID, ANALYSIS, SCORE_VALUE_BAD, MAX_SCORE_VALUE, CRITERIA_DESCRIPTION_BASE, JUSTIFICATION_NEGATIVE, USER_CRITERIA_ID)
    );
    private final ArgumentCaptor<Analysis> analysisArgumentCaptor = ArgumentCaptor.forClass(Analysis.class);
    private final ArgumentCaptor<List<CriteriaAnalysis>> criteriaAnalysisCaptor = ArgumentCaptor.forClass(List.class);
    private CriteriaAnalysisRepository criteriaAnalysisRepository = mock(CriteriaAnalysisRepository.class);
    private AnalysisRepository analysisRepository = mock(AnalysisRepository.class);
    private CvRepository cvRepository = mock(CvRepository.class);
    private RoleRepository roleRepository = mock(RoleRepository.class);
    private AnalysisDal analysisDal = new AnalysisDalImpl(criteriaAnalysisRepository, analysisRepository, cvRepository, roleRepository);

    @BeforeEach
    void before() {
        criteriaAnalysisRepository = mock(CriteriaAnalysisRepository.class);
        analysisRepository = mock(AnalysisRepository.class);
        cvRepository = mock(CvRepository.class);
        roleRepository = mock(RoleRepository.class);
        analysisDal = new AnalysisDalImpl(criteriaAnalysisRepository, analysisRepository, cvRepository, roleRepository);
    }

    @Test
    void testGetResultByIdReturnsTheCorrectResponse() {
        when(analysisRepository.findAnalysedCandidateName(ANALYSIS_ID)).thenReturn(CANDIDATE_NAME);
        when(criteriaAnalysisRepository.findAllByAnalysisId(ANALYSIS_ID)).thenReturn(CRITERIA_ANALYSIS_LIST);
        CandidateScores candidateScores = analysisDal.getResultById(ANALYSIS_ID);
        assertEquals(CANDIDATE_SCORES, candidateScores);
    }

    @Test
    void testWhenSaveResultThatItWillSaveTheCorrectResults() {
        Analysis analysis = Analysis.builder().id(ANALYSIS_ID).cv(DATABASE_CV).role(DATABASE_ROLE).userId(USER_ID).build();
        when(analysisRepository.save(analysisArgumentCaptor.capture())).thenReturn(analysis);
        doReturn(DATABASE_CV).when(cvRepository).getReferenceById(CV_ID);
        doReturn(DATABASE_ROLE).when(roleRepository).getReferenceById(ROLE_ID);
        long result = analysisDal.saveResults(USER_ID, CV_ID, ROLE_ID, SCORED_CRITERIA_LIST);
        // Verify the returned response is correct
        assertEquals(ANALYSIS_ID, result);
        // Verify that the saved analysis has the correct values
        Analysis savedAnalysis = analysisArgumentCaptor.getValue();
        assertEquals(USER_ID, savedAnalysis.getUserId());
        assertEquals(DATABASE_ROLE, savedAnalysis.getRole());
        assertEquals(DATABASE_CV, savedAnalysis.getCv());
        // Check that the results are saved
        verify(criteriaAnalysisRepository, atLeastOnce()).saveAll(criteriaAnalysisCaptor.capture());
        List<CriteriaAnalysis> criteriaAnalysisList = criteriaAnalysisCaptor.getValue();
        assertEquals(2, criteriaAnalysisList.size());
        var goodCriteriaAnalysis = criteriaAnalysisList.stream().filter(a -> a.getScore() > 0).findAny().orElseThrow();
        var badCriteriaAnalysis = criteriaAnalysisList.stream().filter(a -> a.getScore() == 0).findAny().orElseThrow();
        assertEquals(SCORE_VALUE_GOOD, goodCriteriaAnalysis.getScore());
        assertEquals(SCORE_VALUE_BAD, badCriteriaAnalysis.getScore());
        assertEquals(USER_CRITERIA_ID2, goodCriteriaAnalysis.getUserCriteriaId());
        assertEquals(USER_CRITERIA_ID, badCriteriaAnalysis.getUserCriteriaId());
        assertEquals(MAX_SCORE_VALUE, goodCriteriaAnalysis.getMaxScore());
        assertEquals(MAX_SCORE_VALUE, badCriteriaAnalysis.getMaxScore());
        assertEquals(CRITERIA_DESCRIPTION, goodCriteriaAnalysis.getCriteriaRequest());
        assertEquals(CRITERIA_DESCRIPTION_BASE, badCriteriaAnalysis.getCriteriaRequest());
        assertEquals(JUSTIFICATION_POSITIVE, goodCriteriaAnalysis.getDescription());
        assertEquals(JUSTIFICATION_NEGATIVE, badCriteriaAnalysis.getDescription());
    }

}
