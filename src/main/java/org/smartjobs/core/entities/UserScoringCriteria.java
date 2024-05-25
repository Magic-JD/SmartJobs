package org.smartjobs.core.entities;

import org.smartjobs.core.service.role.data.CriteriaCategory;

public record UserScoringCriteria(long id, CriteriaCategory category, String criteriaDescription, boolean isBoolean,
                                  int weighting,
                                  String scoringGuide) {
}
