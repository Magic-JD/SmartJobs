package org.smartjobs.adaptors.data;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.smartjobs.adaptors.data.repository.AnalysisRepository;
import org.smartjobs.adaptors.data.repository.CriteriaAnalysisRepository;
import org.smartjobs.adaptors.data.repository.data.Analysis;
import org.smartjobs.adaptors.data.repository.data.CriteriaAnalysis;
import org.smartjobs.core.entities.CandidateScores;
import org.smartjobs.core.ports.dal.AnalysisDal;

import java.util.List;

import static constants.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AnalysisDalImplTest {
    public static final long CRITERIA_ANALYSIS_ID = 534543L;
    public static final List<CriteriaAnalysis> CRITERIA_ANALYSIS_LIST = List.of(
            new CriteriaAnalysis(CRITERIA_ANALYSIS_ID, ANALYSIS_ID, SCORE_VALUE_GOOD, MAX_SCORE_VALUE, CRITERIA_DESCRIPTION, JUSTIFICATION_POSITIVE, USER_CRITERIA_ID),
            new CriteriaAnalysis(CRITERIA_ANALYSIS_ID, ANALYSIS_ID, SCORE_VALUE_BAD, MAX_SCORE_VALUE, CRITERIA_DESCRIPTION, JUSTIFICATION_NEGATIVE, USER_CRITERIA_ID)
    );
    private final CriteriaAnalysisRepository criteriaAnalysisRepository = mock(CriteriaAnalysisRepository.class);
    private final AnalysisRepository analysisRepository = mock(AnalysisRepository.class);
    private final AnalysisDal analysisDal = new AnalysisDalImpl(criteriaAnalysisRepository, analysisRepository);

    private final ArgumentCaptor<Analysis> analysisArgumentCaptor = ArgumentCaptor.forClass(Analysis.class);
    private final ArgumentCaptor<List<CriteriaAnalysis>> criteriaAnalysisCaptor = ArgumentCaptor.forClass(List.class);

    @Test
    void testGetResultByIdReturnsTheCorrectResponse(){
        when(analysisRepository.findAnalysedCandidateName(ANALYSIS_ID)).thenReturn(CANDIDATE_NAME);
        when(criteriaAnalysisRepository.findAllByAnalysisId(ANALYSIS_ID)).thenReturn(CRITERIA_ANALYSIS_LIST);
        CandidateScores candidateScores = analysisDal.getResultById(ANALYSIS_ID);
        assertEquals(CANDIDATE_SCORES, candidateScores);
    }

    @Test
    void testWhenSaveResultThatItWillSaveTheCorrectResults(){
        Analysis analysis = Analysis.builder().id(ANALYSIS_ID).cvId(CV_ID).roleId(ROLE_ID).userId(USER_ID).build();
        when(analysisRepository.save(analysisArgumentCaptor.capture())).thenReturn(analysis);
        long result = analysisDal.saveResults(USER_ID, CV_ID, ROLE_ID, SCORED_CRITERIA_LIST);
        // Verify the returned response is correct
        assertEquals(ANALYSIS_ID, result);
        // Verify that the saved analysis has the correct values
        Analysis savedAnalysis = analysisArgumentCaptor.getValue();
        assertEquals(USER_ID, savedAnalysis.getUserId());
        assertEquals(ROLE_ID, savedAnalysis.getRoleId());
        assertEquals(CV_ID, savedAnalysis.getCvId());
        // Check that the results are saved
        verify(criteriaAnalysisRepository, atLeastOnce()).saveAll(criteriaAnalysisCaptor.capture());
        List<CriteriaAnalysis> criteriaAnalysisList = criteriaAnalysisCaptor.getValue();
        assertEquals(2, criteriaAnalysisList.size());
        var goodCriteriaAnalysis = criteriaAnalysisList.stream().filter(a -> a.getScore() > 0).findAny().orElseThrow();
        var badCriteriaAnalysis = criteriaAnalysisList.stream().filter(a -> a.getScore() == 0).findAny().orElseThrow();
        assertEquals(SCORE_VALUE_GOOD, goodCriteriaAnalysis.getScore());
        assertEquals(SCORE_VALUE_BAD, badCriteriaAnalysis.getScore());
        assertEquals(USER_CRITERIA_ID, goodCriteriaAnalysis.getUserCriteriaId());
        assertEquals(USER_CRITERIA_ID, badCriteriaAnalysis.getUserCriteriaId());
        assertEquals(MAX_SCORE_VALUE, goodCriteriaAnalysis.getMaxScore());
        assertEquals(MAX_SCORE_VALUE, badCriteriaAnalysis.getMaxScore());
        assertEquals(CRITERIA_DESCRIPTION, goodCriteriaAnalysis.getCriteriaRequest());
        assertEquals(CRITERIA_DESCRIPTION, badCriteriaAnalysis.getCriteriaRequest());
        assertEquals(JUSTIFICATION_POSITIVE, goodCriteriaAnalysis.getDescription());
        assertEquals(JUSTIFICATION_NEGATIVE, badCriteriaAnalysis.getDescription());
    }

}