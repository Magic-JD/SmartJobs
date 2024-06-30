package org.smartjobs.core.event.events;

import org.smartjobs.core.event.Event;

public record SendEmailEvent(String email, String verificationCode) implements Event {
}
