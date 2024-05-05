package org.smartjobs.com.client.gpt;

import org.smartjobs.com.service.candidate.data.ProcessedCv;
import org.smartjobs.com.service.role.data.ScoringCriteria;

import java.util.Optional;

public interface AiClient {
    Optional<String> extractCandidateName(String cvData);

    Optional<String> anonymousCandidateDescription(String cvData);

    GptClient.Score scoreForCriteria(ProcessedCv ci, ScoringCriteria scoringCriteria);
}
