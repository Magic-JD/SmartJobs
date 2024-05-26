package org.smartjobs.core.ports.client;

import org.smartjobs.core.entities.Score;

import java.util.Optional;

public interface AiService {
    Optional<String> extractCandidateName(String cv);

    Optional<String> anonymizeCv(String cv);

    Optional<Score> scoreForCriteria(String cv, String criteria, long maxScore);

    Optional<Score> passForCriteria(String cv, String criteria, long maxScore);
}
