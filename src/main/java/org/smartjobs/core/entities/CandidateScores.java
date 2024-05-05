package org.smartjobs.core.entities;

import java.util.List;

public record CandidateScores(String uuid, String name, double percentage, List<ScoredCriteria> scoringCriteria) {
}
