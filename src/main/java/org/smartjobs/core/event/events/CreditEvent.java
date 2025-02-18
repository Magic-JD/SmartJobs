package org.smartjobs.core.event.events;

import org.smartjobs.core.constants.CreditType;
import org.smartjobs.core.event.Event;

public record CreditEvent(long userId, long credits, CreditType creditType) implements Event {
}
