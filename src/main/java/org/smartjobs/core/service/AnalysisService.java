package org.smartjobs.core.service;

import org.smartjobs.core.entities.CandidateScores;
import org.smartjobs.core.entities.ProcessedCv;
import org.smartjobs.core.entities.UserScoringCriteria;

import java.util.List;

public interface AnalysisService {
    List<CandidateScores> scoreToCriteria(long userId, long roleId, List<ProcessedCv> candidateInformation, List<UserScoringCriteria> userScoringCriteria);

    CandidateScores getResultById(long id);
}
