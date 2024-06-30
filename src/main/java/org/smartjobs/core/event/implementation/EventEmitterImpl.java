package org.smartjobs.core.event.implementation;

import org.smartjobs.core.event.Event;
import org.smartjobs.core.event.EventEmitter;
import org.smartjobs.core.ports.listener.Listener;
import org.smartjobs.core.utils.ConcurrencyUtil;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class EventEmitterImpl implements EventEmitter {

    private final Map<Class<? extends Event>, Collection<Listener>> listeners = new ConcurrentHashMap<>();

    @Override
    public void sendEvent(Event event) {
        Collection<Listener> eventListeners = this.listeners.getOrDefault(event.getClass(), Collections.emptyList());
        ConcurrencyUtil.virtualThreadListForEach(eventListeners, listener -> listener.processEvent(event));
    }

    @Override
    public <E extends Event> void registerForEvents(Listener listener, Class<E> eventType) {
        Collection<Listener> eventListeners = listeners.computeIfAbsent(eventType, _ -> new ConcurrentLinkedQueue<>());
        eventListeners.add(listener);
    }
}
