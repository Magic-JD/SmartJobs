package org.smartjobs.core.entities;

import java.util.List;

public record Role(long id, String position, List<UserScoringCriteria> userScoringCriteria) {
}
