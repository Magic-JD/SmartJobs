package org.smartjobs.core.ports.listener;

import org.smartjobs.core.event.Event;

public interface Listener {

    void processEvent(Event event);

}
