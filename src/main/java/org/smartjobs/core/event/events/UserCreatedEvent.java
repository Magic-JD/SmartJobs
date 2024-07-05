package org.smartjobs.core.event.events;

import org.smartjobs.core.event.Event;

public record UserCreatedEvent(long userId) implements Event {
}
