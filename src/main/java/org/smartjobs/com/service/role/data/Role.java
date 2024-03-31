package org.smartjobs.com.service.role.data;

import java.util.List;

public record Role(String position, List<ScoringCriteria> scoringCriteria) {
}
