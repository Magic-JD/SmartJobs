package org.smartjobs.core.ports.dal;

import org.smartjobs.adaptors.data.AnalysisDalImpl.CandidateDisplay;
import org.smartjobs.core.entities.ScoredCriteria;

import java.util.List;

public interface AnalysisDal {

    CandidateDisplay getResultById(long id);

    long saveResults(long userId, long cvId, long roleId, List<ScoredCriteria> clearResults);
}
