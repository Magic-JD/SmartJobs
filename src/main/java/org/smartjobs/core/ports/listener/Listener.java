package org.smartjobs.core.ports.listener;

import org.smartjobs.core.service.event.events.Event;

public interface Listener {

    void processEvent(Event event);

}
