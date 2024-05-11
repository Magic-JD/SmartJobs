package org.smartjobs.core.service.sse;

import lombok.extern.slf4j.Slf4j;
import org.smartjobs.core.service.SseService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@Slf4j
public class SseServiceImpl implements SseService {

    private final ConcurrentMap<String, SseEmitter> sseEmitters = new ConcurrentHashMap<>();

    @Override
    public SseEmitter register(String username) {
        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);
        sseEmitters.put(username, sseEmitter);
        log.info("Adding SseEmitter for user: {}", username);
        sseEmitter.onCompletion(() -> log.info("SseEmitter is completed"));
        sseEmitter.onTimeout(() -> log.info("SseEmitter is timed out"));
        return sseEmitter;
    }

    @Override
    public void send(String username, String messageName, String content) {
        var emitter = sseEmitters.get(username);
        if (emitter == null) {
            log.error("Update {} never reached user {}", messageName, username);
            return;
        }
        try {
            emitter.send(SseEmitter.event().name(messageName).data(content));
        } catch (IOException e) {
            log.error("Update {} never reached user {}", messageName, username);
        }
    }

    @Scheduled(fixedRate = 1000)
    private void heartBeat() {
        for (Entry<String, SseEmitter> emitter : sseEmitters.entrySet()) {
            try {
                emitter.getValue().send(SseEmitter.event().name("heart-beat").data(System.currentTimeMillis()));
            } catch (IOException e) {
                String username = emitter.getKey();
                log.info("Heartbeat lost, removing user {}", username);
                sseEmitters.remove(username);
            }
        }
    }

}
