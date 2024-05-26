package org.smartjobs.core.entities;

public record ScoredCriteria(long userCriteriaId, String criteriaRequest, String justification, double score,
                             long maxScore) {
}
