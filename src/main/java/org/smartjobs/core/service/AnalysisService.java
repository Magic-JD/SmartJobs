package org.smartjobs.core.service;

import org.smartjobs.core.entities.CandidateScores;
import org.smartjobs.core.entities.ProcessedCv;
import org.smartjobs.core.entities.ScoringCriteria;

import java.util.List;

public interface AnalysisService {
    List<CandidateScores> scoreToCriteria(long userId, long roleId, List<ProcessedCv> candidateInformation, List<ScoringCriteria> scoringCriteria);

    CandidateScores getResultById(long id);
}
