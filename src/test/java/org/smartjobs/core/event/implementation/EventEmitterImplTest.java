package org.smartjobs.core.event.implementation;

import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.Test;
import org.smartjobs.core.event.events.ErrorEvent;

import java.util.Collections;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class EventEmitterImplTest {

    EventEmitterImpl emitter = new EventEmitterImpl();

    @Test
    void testAllListenersAreNotifiedWhenAnEventIsShared(){
        CountDownLatch countDownLatch = new CountDownLatch(3);
        emitter.registerForEvents(_ -> countDownLatch.countDown());
        emitter.registerForEvents(_ -> countDownLatch.countDown());
        emitter.registerForEvents(_ -> countDownLatch.countDown());
        emitter.sendEvent(new ErrorEvent(0, Collections.emptyList()));
        try {
            assertTrue(countDownLatch.await(10, TimeUnit.MILLISECONDS), "Not all the listeners recieved events");
        } catch (InterruptedException e) {
            fail("The query was inturrupted");
        }
    }

}