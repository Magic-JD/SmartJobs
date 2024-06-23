package org.smartjobs.adaptors.view.web.controller.sse;

import display.CamelCaseDisplayNameGenerator;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.smartjobs.adaptors.view.web.service.sse.SseServiceImpl;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static constants.TestConstants.USER;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayNameGeneration(CamelCaseDisplayNameGenerator.class)
class SseControllerTest {

    public static final SseServiceImpl SSE_SERVICE = new SseServiceImpl();
    SseController sseController = new SseController(SSE_SERVICE);

    @Test
    void testRegisterCanBeCalledAndProvidesAValidEmmitter() {
        SseEmitter emitter = sseController.register(USER);
        assertNotNull(emitter);
    }

}