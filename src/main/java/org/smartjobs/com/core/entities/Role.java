package org.smartjobs.com.core.entities;

import java.util.List;

public record Role(long id, String position, List<ScoringCriteria> scoringCriteria) {
}
