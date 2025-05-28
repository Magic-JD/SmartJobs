package org.smartjobs.adaptors.service.email;

import org.smartjobs.core.event.Event;
import org.smartjobs.core.event.EventEmitter;
import org.smartjobs.core.event.events.IssueCouponEvent;
import org.smartjobs.core.event.events.ValidateEmailEvent;
import org.smartjobs.core.ports.listener.Listener;

public abstract class EmailService implements Listener {

    EmailService(EventEmitter emitter) {
        emitter.registerForEvents(this, ValidateEmailEvent.class);
        emitter.registerForEvents(this, IssueCouponEvent.class);
    }

    abstract void createVerificationEmail(String email, String verificationCode);

    abstract void createIssueCouponEmail(String email, String couponCode);


    @Override
    public void processEvent(Event event) {
        switch (event) {
            case ValidateEmailEvent(String email, String code) -> createVerificationEmail(email, code);
            case IssueCouponEvent(String email, String code) -> createIssueCouponEmail(email, code);
            default -> throw new UnsupportedOperationException();
        }
    }
}
