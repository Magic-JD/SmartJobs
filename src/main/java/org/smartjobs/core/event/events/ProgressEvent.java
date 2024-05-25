package org.smartjobs.core.event.events;

import org.smartjobs.core.event.Event;

public record ProgressEvent(long userId, int currentState, int finalState) implements Event {
}
