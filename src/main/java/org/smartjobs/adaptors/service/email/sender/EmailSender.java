package org.smartjobs.adaptors.service.email.sender;

import jakarta.mail.MessagingException;

public interface EmailSender {

    void sendEmail(String email, String messageText) throws MessagingException;

}
