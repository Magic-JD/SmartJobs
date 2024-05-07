package org.smartjobs.core.entities;

import org.smartjobs.core.service.role.data.CriteriaCategory;

import java.util.Optional;

public record DefinedScoringCriteria(
        long id,
        String description,
        CriteriaCategory category,
        boolean needsInput,
        Optional<String> inputExample,
        String aiPrompt,
        String tooltip
) {
}
