package org.smartjobs.adaptors.view.web.service.sse;

import lombok.extern.slf4j.Slf4j;
import org.smartjobs.adaptors.view.web.service.SseService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

@Service
@Slf4j
public class SseServiceImpl implements SseService {

    private final ConcurrentMap<Long, ConcurrentLinkedQueue<SseEmitter>> sseEmitters = new ConcurrentHashMap<>();

    @Override
    public SseEmitter register(long userId) {
        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);
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

    @Scheduled(fixedRate = 1000)
    private void heartBeat() {
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

    private record Link(long userId, SseEmitter emitter) {
    }

}
