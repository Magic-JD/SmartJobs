package org.smartjobs.com.core.entities;

import java.util.List;

public record ScoringCriteriaResult(String uuid, String name, double percentage,
                                    List<Score> scoringCriteria) {
}
