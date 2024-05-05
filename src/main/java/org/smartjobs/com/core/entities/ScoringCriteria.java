package org.smartjobs.com.core.entities;

import org.smartjobs.com.core.service.role.data.CriteriaCategory;

public record ScoringCriteria(long id, CriteriaCategory category, String criteria, int weighting, String aiPrompt) {
}
