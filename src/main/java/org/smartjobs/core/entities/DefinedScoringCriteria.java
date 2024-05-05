package org.smartjobs.core.entities;

import org.smartjobs.core.service.role.data.CriteriaCategory;

public record DefinedScoringCriteria(
        long id,
        String description,
        CriteriaCategory category,
        boolean needsInput,
        String aiPrompt) {
}
