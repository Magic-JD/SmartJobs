package org.smartjobs.com.service.role.data;

import org.smartjobs.com.exception.categories.ApplicationExceptions.DatabaseEnumCompatibilityException;

import java.util.Arrays;

public enum CriteriaCategory {
    SOFT_SKILLS("Soft Skills"),
    HARD_SKILLS("Hard Skills"),
    RELEVANT_EXPERIENCE("Relevant Experience"),
    QUALIFICATIONS("Qualifications"),
    PROFESSIONAL_ENGAGEMENT_AND_RECOGNITION("Professional Engagement and Recognition");

    private final String name;

    CriteriaCategory(String name) {
        this.name = name;
    }

    public static CriteriaCategory getFromName(String name) {
        return Arrays.stream(CriteriaCategory.values())
                .filter(v -> v.name.equals(name))
                .findFirst()
                .orElseThrow(() -> new DatabaseEnumCompatibilityException(name, CriteriaCategory.class.getName()));
    }

    @Override
    public String toString() {
        return name;
    }
}
