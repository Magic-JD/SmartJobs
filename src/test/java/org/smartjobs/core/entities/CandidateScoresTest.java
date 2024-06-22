package org.smartjobs.core.entities;

import display.CamelCaseDisplayNameGenerator;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static constants.TestConstants.CANDIDATE_ID;
import static constants.TestConstants.CANDIDATE_NAME;
import static java.lang.Double.NaN;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayNameGeneration(CamelCaseDisplayNameGenerator.class)
class CandidateScoresTest {

    public static final List<ScoredCriteria> SCORED_CRITERIA = List.of(
            new ScoredCriteria(0L, "", "", 5.0, 10L),
            new ScoredCriteria(0L, "", "", 15.0, 30L),
            new ScoredCriteria(0L, "", "", 2.5, 5L)
    );

    @Test
    void testThatNaNIsReturnedForPercentageWhenThereAreNoCandidateScores(){
        CandidateScores candidateScores = new CandidateScores(CANDIDATE_ID, CANDIDATE_NAME, Collections.emptyList());
        assertEquals(NaN, candidateScores.percentage());
    }

    @Test
    void testThatTheCorrectPercentageIsReturnedWhenThereAreMultipleCandidateScoresAdded(){
        CandidateScores candidateScores = new CandidateScores(CANDIDATE_ID, CANDIDATE_NAME, SCORED_CRITERIA);
        assertEquals(50, candidateScores.percentage());
    }
}
