package org.smartjobs.core.service;

import org.smartjobs.core.service.event.events.Event;
import org.smartjobs.core.service.event.listener.Listener;

public interface EventService {

    void sendEvent(Event event);

    void registerForEvents(Listener listener);

}
