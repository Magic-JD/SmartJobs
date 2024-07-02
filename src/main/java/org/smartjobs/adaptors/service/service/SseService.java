package org.smartjobs.adaptors.service.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface SseService {
    SseEmitter register(long userId);

    void send(long userId, String messageName, String content);
}
