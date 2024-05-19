package org.smartjobs.adaptors.view.web.controller.sse;

import lombok.extern.slf4j.Slf4j;
import org.smartjobs.core.entities.User;
import org.smartjobs.core.service.SseService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Controller
@RequestMapping("/sse")
@Slf4j
public class SseController {


    private final SseService sseService;

    public SseController(SseService sseService) {
        this.sseService = sseService;
    }

    @GetMapping("/register")
    public SseEmitter register(@AuthenticationPrincipal User user) {
        return sseService.register(user.getId());
    }

}
