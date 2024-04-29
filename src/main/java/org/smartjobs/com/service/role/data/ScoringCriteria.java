package org.smartjobs.com.service.role.data;

public record ScoringCriteria(String description, int weight, CriteriaCategory category, boolean absolute) {
}
