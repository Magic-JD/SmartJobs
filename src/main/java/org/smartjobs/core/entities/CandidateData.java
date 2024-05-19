package org.smartjobs.core.entities;

public record CandidateData(long id, String name, long userId, long roleId, boolean currentlySelected) {
}
