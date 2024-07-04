package org.smartjobs.adaptors.service.email;

import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.smartjobs.adaptors.service.email.sender.EmailSender;
import org.smartjobs.core.event.Event;
import org.smartjobs.core.event.EventEmitter;
import org.smartjobs.core.event.events.IssueCouponEvent;
import org.smartjobs.core.event.events.ValidateEmailEvent;
import org.smartjobs.core.ports.listener.Listener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService implements Listener {

    private final String siteDomain;
    private final EmailSender emailSender;

    public EmailService(EventEmitter emitter, @Value("${site.domain}") String siteDomain, EmailSender emailSender) {
        this.siteDomain = siteDomain;
        this.emailSender = emailSender;
        emitter.registerForEvents(this, ValidateEmailEvent.class);
        emitter.registerForEvents(this, IssueCouponEvent.class);
    }


    public void createVerificationEmail(String email, String verificationCode) {
        log.info("Starting to send email for {}", verificationCode);
        String msg = STR. """
            <a href="https://\{ siteDomain }/login/verify/\{ verificationCode }">
                Please click this link to verify your email.
            </a>""" ;
        try {
            emailSender.sendEmail(email, msg);
            log.info("Email sent");
        } catch (MessagingException e) {
            log.error(STR."Email could not be sent for {}", verificationCode);
        }
    }

    public void createIssueCouponEmail(String email, String couponCode) {
        log.info("Starting to send email for {}", couponCode);
        String msg = STR. """
            Your coupon for SmartJobs credit is : \{ couponCode }
            """ ;
        try {
            emailSender.sendEmail(email, msg);
            log.info("Email sent");
        } catch (MessagingException e) {
            log.error(STR."Email could not be sent for {}", couponCode);
        }
    }

    @Override
    public void processEvent(Event event) {
        switch (event) {
            case ValidateEmailEvent(String email, String code) -> createVerificationEmail(email, code);
            case IssueCouponEvent(String email, String code) -> createIssueCouponEmail(email, code);
            default -> throw new UnsupportedOperationException();
        }
    }
}
