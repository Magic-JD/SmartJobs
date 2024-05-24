package org.smartjobs.adaptors.view.web.service.listener;

import org.smartjobs.adaptors.view.web.constants.DisplayMappings;
import org.smartjobs.adaptors.view.web.service.SseService;
import org.smartjobs.core.service.EventService;
import org.smartjobs.core.service.event.events.CreditEvent;
import org.smartjobs.core.service.event.events.ErrorEvent;
import org.smartjobs.core.service.event.events.Event;
import org.smartjobs.core.service.event.events.ProgressEvent;
import org.smartjobs.core.service.event.listener.Listener;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.text.DecimalFormat;
import java.util.stream.Collectors;

import static org.smartjobs.adaptors.view.web.constants.ThymeleafConstants.ERROR_BOX;

@Component
public class ListenerImpl implements Listener {

    private final SseService sseService;
    private final TemplateEngine templateEngine;
    private final DecimalFormat decimalFormat;

    public ListenerImpl(SseService sseService, EventService eventService, TemplateEngine templateEngine, DecimalFormat decimalFormat) {
        this.sseService = sseService;
        this.templateEngine = templateEngine;
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
            case ErrorEvent(var userId, var processingFailures) -> {
                Context context = new Context();
                context.setVariable("message", processingFailures.stream().map(DisplayMappings::mapProcessingFailure).collect(Collectors.joining("\n")));
                String errorBoxHtml = templateEngine.process(ERROR_BOX, context);
                sseService.send(userId, "message", errorBoxHtml);
            }
            default -> throw new UnsupportedOperationException();
        }
    }
}
