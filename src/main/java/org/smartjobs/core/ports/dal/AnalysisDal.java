package org.smartjobs.core.ports.dal;

import org.smartjobs.core.entities.CandidateScores;
import org.smartjobs.core.entities.ScoredCriteria;

import java.util.List;

public interface AnalysisDal {

    CandidateScores getResultById(long id);

    long saveResults(long userId, long cvId, long roleId, List<ScoredCriteria> clearResults);
}
