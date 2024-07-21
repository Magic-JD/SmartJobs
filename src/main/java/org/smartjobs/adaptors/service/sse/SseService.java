package org.smartjobs.adaptors.service.sse;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface SseService {
    SseEmitter register(long userId);

    void send(long userId, String messageName, String content);
}
