package org.smartjobs.core.service;

import org.smartjobs.core.ports.listener.Listener;
import org.smartjobs.core.service.event.events.Event;

public interface EventService {

    void sendEvent(Event event);

    void registerForEvents(Listener listener);

}
