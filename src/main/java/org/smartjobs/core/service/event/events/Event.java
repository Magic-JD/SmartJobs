package org.smartjobs.core.service.event.events;

public interface Event {

    EventType getEventType();

    long getUserId();

}
