package org.smartjobs.core.service.event.events;

public record ProgressEvent(long userId, int currentState, int finalState) implements Event {
}
