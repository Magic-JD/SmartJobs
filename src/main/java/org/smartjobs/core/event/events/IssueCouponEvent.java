package org.smartjobs.core.event.events;

import org.smartjobs.core.event.Event;

public record IssueCouponEvent(String email, String coupon) implements Event {
}
