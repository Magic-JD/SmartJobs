package org.smartjobs.com.service.role.data;

public record DefinedScoringCriteria(
        long id,
        String description,
        CriteriaCategory category,
        boolean needsInput,
        String aiPrompt) {
}
