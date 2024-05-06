package org.smartjobs.core.entities;

import org.smartjobs.core.service.role.data.CriteriaCategory;

public record ScoringCriteria(long id, CriteriaCategory category, String name, boolean isBoolean, int weighting,
                              String scoringGuide) {
}
