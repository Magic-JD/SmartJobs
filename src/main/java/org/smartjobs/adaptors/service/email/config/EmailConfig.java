package org.smartjobs.adaptors.service.email.config;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import lombok.extern.slf4j.Slf4j;
import org.smartjobs.adaptors.service.email.sender.EmailSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Properties;

@Configuration
@Slf4j
public class EmailConfig {

    private static final String SUBJECT = "Smart Jobs email Verification";

    @Bean
    public Session session(@Value("${mail.smtp.host}") String host,
                           @Value("${mail.smtp.port}") String port,
                           @Value("${mail.smtp.username}") String username,
                           @Value("${mail.smtp.password}") String password) {
        Properties prop = new Properties();
        prop.put("mail.smtp.auth", true);
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.host", host);
        prop.put("mail.smtp.port", port);
        prop.put("mail.smtp.ssl", "no");
        prop.put("mail.smtp.tls", "yes");
        prop.put("mail.smtp.ssl.trust", host);
        return Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }

    @Bean
    @Profile("prod")
    public EmailSender emailSender(Session session, @Value("${site.email}") String sendingEmail) {
        return (email, messageText) -> {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(sendingEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject(SUBJECT);
            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(messageText, "text/html; charset=utf-8");
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(mimeBodyPart);
            message.setContent(multipart);
            Transport.send(message);
        };
    }

    @Bean
    @Profile({"dev", "test"})
    public EmailSender fakeEmailSender(@Value("${site.email}") String sendingEmail) {
        return (email, messageText) -> {
            log.debug("Sending email from {} to {}", sendingEmail, email);
            log.debug("Content: {}", messageText);
        };
    }
}
