package org.smartjobs.core.service.event;

import org.smartjobs.core.ports.listener.Listener;
import org.smartjobs.core.service.EventService;
import org.smartjobs.core.service.event.events.Event;
import org.smartjobs.core.utils.ConcurrencyUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EventServiceImpl implements EventService {

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
