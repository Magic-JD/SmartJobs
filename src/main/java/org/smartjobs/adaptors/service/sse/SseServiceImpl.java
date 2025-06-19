package org.smartjobs.adaptors.service.sse;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.smartjobs.adaptors.view.web.constants.DisplayMappings;
import org.smartjobs.core.event.Event;
import org.smartjobs.core.event.EventEmitter;
import org.smartjobs.core.event.events.CreditEvent;
import org.smartjobs.core.event.events.ErrorEvent;
import org.smartjobs.core.event.events.ProgressEvent;
import org.smartjobs.core.ports.listener.Listener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import static org.smartjobs.adaptors.view.web.constants.ThymeleafConstants.ERROR_BOX;

@Service
@Slf4j
public class SseServiceImpl implements SseService, Listener {


    private final TemplateEngine templateEngine;
    private final DecimalFormat decimalFormat;
    private final ConcurrentMap<Long, ConcurrentLinkedQueue<SseEmitter>> sseEmitters = new ConcurrentHashMap<>();

    public SseServiceImpl(EventEmitter eventEmitter, TemplateEngine templateEngine, DecimalFormat decimalFormat) {
        this.templateEngine = templateEngine;
        this.decimalFormat = decimalFormat;
        eventEmitter.registerForEvents(this, ProgressEvent.class);
        eventEmitter.registerForEvents(this, CreditEvent.class);
        eventEmitter.registerForEvents(this, ErrorEvent.class);
    }

    @Override
    public void processEvent(Event event) {
        switch (event) {
            case ProgressEvent(var userId, var currentState, var finalState) ->
                    send(userId, "progress", STR. "\{ currentState }/\{ finalState }" );
            case CreditEvent(var userId, var currentCredit, var creditType) ->
                    send(userId, "credit", STR. "\{ decimalFormat.format(currentCredit) }" );
            case ErrorEvent(var userId, var processingFailures) -> {
                Context context = new Context();
                context.setVariable("message", processingFailures.stream().map(DisplayMappings::mapProcessingFailure).collect(Collectors.joining("\n")));
                String errorBoxHtml = templateEngine.process(ERROR_BOX, context);
                send(userId, "message", errorBoxHtml);
            }
            default -> throw new UnsupportedOperationException();
        }
    }


    @Override
    public SseEmitter register(long userId) {
        SseEmitter sseEmitter = new SseEmitter(5000L);
        var current = sseEmitters.getOrDefault(userId, new ConcurrentLinkedQueue<>());
        current.add(sseEmitter);
        sseEmitters.put(userId, current);
        log.info("Adding SseEmitter for user: {}", userId);
        sseEmitter.onCompletion(() -> log.info("SseEmitter is completed"));
        sseEmitter.onTimeout(() -> log.info("SseEmitter is timed out"));
        return sseEmitter;
    }

    @Override
    public void send(long userId, String messageName, String content) {
        var emitters = sseEmitters.get(userId);
        if (emitters == null) {
            log.error("Update {} never reached user {}", messageName, userId);
            return;
        }

        emitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event().name(messageName).data(content));
            } catch (IOException | IllegalStateException e) {
                log.error("Update {} never reached user {}", messageName, userId);
            }
        });
    }

    @Scheduled(fixedRate = 100_000)
    protected void heartBeat() { //Needs to be protected to be able to be scheduled
        List<Link> list = sseEmitters.entrySet().stream().flatMap(entry -> {
            long key = entry.getKey();
            return entry.getValue().stream().map(e -> new Link(key, e));
        }).toList();
        list.forEach(link -> {
            try {
                link.emitter().send(SseEmitter.event().name("heart-beat").data(System.currentTimeMillis()));
            } catch (IOException | IllegalStateException e) {
                long userId = link.userId();
                log.info("Heartbeat lost, removing user {}", userId);
                ConcurrentLinkedQueue<SseEmitter> currentList = sseEmitters.get(userId);
                currentList.remove(link.emitter());
                if (currentList.isEmpty()) {
                    sseEmitters.remove(userId);
                }
            }
        });
    }

    @PreDestroy
    public void shutdown() {
        log.info("Shutting down, cleaning up SSE emitters");
        sseEmitters.values()
                .stream()
                .flatMap(Collection::stream)
                .forEach(SseEmitter::complete);
        sseEmitters.clear();
    }

    private record Link(long userId, SseEmitter emitter) { }
}
