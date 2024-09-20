package shop.com.Services;

import jakarta.mail.MessagingException;
import shop.com.DTO.Mail;

public interface  IEmailService {
    void sendMail(Mail mail) throws MessagingException;
}
