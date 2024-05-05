package org.smartjobs.core.client;

import org.smartjobs.core.entities.Score;

import java.util.Optional;

public interface AiClient {
    Optional<String> extractCandidateName(String cv);

    Optional<String> anonymizeCv(String cv);

    Optional<Score> scoreForCriteria(String cv, String criteria, int maxScore);
}
