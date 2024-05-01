package org.smartjobs.com.service.role.data;

public record ScoringCriteria(CriteriaCategory category, String criteria, int weighting, String aiPrompt) {
}
