package org.smartjobs.core.event.implementation;

import org.smartjobs.core.event.Event;
import org.smartjobs.core.event.EventEmitter;
import org.smartjobs.core.ports.listener.Listener;
import org.smartjobs.core.utils.ConcurrencyUtil;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class EventEmitterImpl implements EventEmitter {

    private final List<Listener> listeners = new ArrayList<>();

    @Override
    public void sendEvent(Event event) {
        ConcurrencyUtil.virtualThreadListForEach(listeners, listener -> listener.processEvent(event));
    }

    @Override
    public void registerForEvents(Listener listener) {
        listeners.add(listener);
    }
}
