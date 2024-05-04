package org.smartjobs.com.service.role.data;

public record ScoringCriteria(long id, CriteriaCategory category, String criteria, int weighting, String aiPrompt) {
}
