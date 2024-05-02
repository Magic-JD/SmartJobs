package org.smartjobs.com.service.criteria.data;

import org.smartjobs.com.service.role.data.CriteriaCategory;

public record DefinedScoringCriteria(
        long id,
        String description,
        CriteriaCategory category,
        boolean needsInput,
        String aiPrompt) {
}
