package org.smartjobs.core.entities;

import java.util.Optional;

public record UserCriteria(long id, long definedCriteriaId, Optional<String> value, int score) {
}
