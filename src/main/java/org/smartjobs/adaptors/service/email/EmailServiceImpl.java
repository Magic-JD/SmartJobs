package org.smartjobs.adaptors.service.email;

import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.smartjobs.adaptors.service.email.sender.EmailSender;
import org.smartjobs.core.event.EventEmitter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailServiceImpl extends EmailService {

    private final String siteDomain;
    private final EmailSender emailSender;

    public EmailServiceImpl(EventEmitter emitter, @Value("${site.domain}") String siteDomain, EmailSender emailSender) {
        super(emitter);
        this.siteDomain = siteDomain;
        this.emailSender = emailSender;

    }


    @Override
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

    @Override
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
}
