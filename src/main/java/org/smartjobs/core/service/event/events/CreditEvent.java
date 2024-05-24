package org.smartjobs.core.service.event.events;

import org.smartjobs.core.entities.CreditType;

public record CreditEvent(long userId, long credits, CreditType creditType) implements Event {
}
