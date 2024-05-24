package org.smartjobs.adaptors.view.web.service.listener;

import org.smartjobs.adaptors.view.web.service.SseService;
import org.smartjobs.core.service.EventService;
import org.smartjobs.core.service.event.events.Event;
import org.smartjobs.core.service.event.listener.Listener;
import org.springframework.stereotype.Component;

@Component
public class ListenerImpl implements Listener {

    private final SseService sseService;

    public ListenerImpl(SseService sseService, EventService eventService) {
        this.sseService = sseService;
        eventService.registerForEvents(this);
    }

    @Override
    public void processEvent(Event event) {
        long userId = event.getUserId();
        switch (event.getEventType()) {
            case CREDIT -> sseService.send(userId, "credit", "");
            case ERROR -> sseService.send(userId, "error", "");
            case PROGRESS -> sseService.send(userId, "progress", "");
        }
    }
}
