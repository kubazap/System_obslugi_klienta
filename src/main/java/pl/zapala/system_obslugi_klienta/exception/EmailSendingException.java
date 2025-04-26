package pl.zapala.system_obslugi_klienta.exception;

import jakarta.mail.MessagingException;
public class EmailSendingException extends RuntimeException {
    public EmailSendingException(String message) {
        super(message);
    }
    public EmailSendingException(String message, Throwable cause) {
        super(message, cause);
    }
    public EmailSendingException(String message, MessagingException cause) {
        super(message, cause);
    }
}
