package org.smartjobs.core.entities;

public record ScoredCriteria(String criteriaRequest, String justification, double score, int maxScore) {
}
