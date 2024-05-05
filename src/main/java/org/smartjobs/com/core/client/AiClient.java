package org.smartjobs.com.core.client;

import org.smartjobs.com.core.entities.ProcessedCv;
import org.smartjobs.com.core.entities.Score;
import org.smartjobs.com.core.entities.ScoringCriteria;

import java.util.Optional;

public interface AiClient {
    Optional<String> extractCandidateName(String cvData);

    Optional<String> anonymousCandidateDescription(String cvData);

    Score scoreForCriteria(ProcessedCv ci, ScoringCriteria scoringCriteria);
}
