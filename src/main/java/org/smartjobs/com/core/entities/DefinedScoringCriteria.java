package org.smartjobs.com.core.entities;

import org.smartjobs.com.core.service.role.data.CriteriaCategory;

public record DefinedScoringCriteria(
        long id,
        String description,
        CriteriaCategory category,
        boolean needsInput,
        String aiPrompt) {
}
