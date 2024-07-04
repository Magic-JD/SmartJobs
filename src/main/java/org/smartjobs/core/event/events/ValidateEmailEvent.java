package org.smartjobs.core.event.events;

import org.smartjobs.core.event.Event;

public record ValidateEmailEvent(String email, String verificationCode) implements Event {
}
