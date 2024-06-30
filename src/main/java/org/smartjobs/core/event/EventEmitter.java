package org.smartjobs.core.event;

import org.smartjobs.core.ports.listener.Listener;

public interface EventEmitter {

    void sendEvent(Event event);

    <E extends Event> void registerForEvents(Listener listener, Class<E> eventType);

}
