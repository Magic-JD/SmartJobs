package org.smartjobs.core.client;

import org.smartjobs.core.entities.ProcessedCv;
import org.smartjobs.core.entities.Score;
import org.smartjobs.core.entities.ScoringCriteria;

import java.util.Optional;

public interface AiClient {
    Optional<String> extractCandidateName(String cvData);

    Optional<String> anonymousCandidateDescription(String cvData);

    Score scoreForCriteria(ProcessedCv ci, ScoringCriteria scoringCriteria);
}
