package org.smartjobs.core.entities;

import org.smartjobs.core.service.role.data.CriteriaCategory;

public record ScoringCriteria(long id, CriteriaCategory category, String criteria, int weighting, String aiPrompt) {
}
