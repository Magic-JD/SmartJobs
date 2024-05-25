package org.smartjobs.core.event.events;

import org.smartjobs.core.event.Event;
import org.smartjobs.core.failures.ProcessFailure;

import java.util.List;

public record ErrorEvent(long userId, List<ProcessFailure> processFailure) implements Event {
}
