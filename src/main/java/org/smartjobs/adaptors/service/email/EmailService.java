package org.smartjobs.adaptors.service.email;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import lombok.extern.slf4j.Slf4j;
import org.smartjobs.core.event.Event;
import org.smartjobs.core.event.EventEmitter;
import org.smartjobs.core.event.events.SendEmailEvent;
import org.smartjobs.core.ports.listener.Listener;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
@Slf4j
public class EmailService implements Listener {

    public EmailService(EventEmitter emitter) {
        emitter.registerForEvents(this);
    }


    public void sendEmail(String email, String verificationCode) {
        log.info("Starting to send email");
        Properties prop = new Properties();
        prop.put("mail.smtp.auth", true);
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.host", "sandbox.smtp.mailtrap.io");
        prop.put("mail.smtp.port", "2525");
        prop.put("mail.smtp.ssl", "no");
        prop.put("mail.smtp.tls", "yes");
        prop.put("mail.smtp.ssl.trust", "sandbox.smtp.mailtrap.io");
        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("be8c9ca4aed8a4", "ecd6827edace5d");
            }
        });
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("from@gmail.com"));
            message.setRecipients(
                    Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("Smart Jobs email Verification");

            String msg = "Your verification code is: " + verificationCode;

            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(msg, "text/html; charset=utf-8");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(mimeBodyPart);

            message.setContent(multipart);

            Transport.send(message);
        } catch (MessagingException e) {
            log.error(STR. "Email could not be sent to \{ email }" );
        }

    }

    @Override
    public void processEvent(Event event) {
        if (event instanceof SendEmailEvent s) {
            sendEmail(s.email(), s.verificationCode());
        }
    }
}
