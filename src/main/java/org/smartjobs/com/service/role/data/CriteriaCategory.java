package org.smartjobs.com.service.role.data;

import org.smartjobs.com.exception.categories.ApplicationExceptions.DatabaseEnumCompatibilityException;

import java.util.Arrays;

public enum CriteriaCategory {
    SOFT_SKILLS("Soft Skills"),
    HARD_SKILLS("Hard Skills");

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
