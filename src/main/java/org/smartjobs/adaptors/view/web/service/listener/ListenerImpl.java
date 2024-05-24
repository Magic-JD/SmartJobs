package org.smartjobs.adaptors.view.web.service.listener;

import org.smartjobs.adaptors.view.web.service.SseService;
import org.smartjobs.core.service.EventService;
import org.smartjobs.core.service.event.events.CreditEvent;
import org.smartjobs.core.service.event.events.Event;
import org.smartjobs.core.service.event.events.ProgressEvent;
import org.smartjobs.core.service.event.listener.Listener;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;

@Component
public class ListenerImpl implements Listener {

    private final SseService sseService;
    private final DecimalFormat decimalFormat;

    public ListenerImpl(SseService sseService, EventService eventService, DecimalFormat decimalFormat) {
        this.sseService = sseService;
        this.decimalFormat = decimalFormat;
        eventService.registerForEvents(this);
    }

    @Override
    public void processEvent(Event event) {
        switch (event) {
            case ProgressEvent(var userId, var currentState, var finalState) ->
                    sseService.send(userId, "progress", STR. "\{ currentState }/\{ finalState }" );
            case CreditEvent(var userId, var currentCredit, var creditType) ->
                    sseService.send(userId, "credit", STR. "Credit: \{ decimalFormat.format(currentCredit) }" );
            default -> throw new UnsupportedOperationException();
        }
    }
}
