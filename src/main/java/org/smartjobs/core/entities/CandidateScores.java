package org.smartjobs.core.entities;

import java.util.List;

public record CandidateScores(long id, String name, double percentage, List<ScoredCriteria> scoringCriteria) {
}
