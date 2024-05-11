package org.smartjobs.core.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface SseService {
    SseEmitter register(String username);

    void send(String username, String messageName, String content);
}
