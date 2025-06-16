package pl.zapala.system_obslugi_klienta.exception;

import jakarta.mail.MessagingException;

/**
 * Wyjątek sygnalizujący błąd podczas wysyłania wiadomości e-mail.
 * Rzucany, gdy nie uda się poprawnie zbudować lub wysłać maila.
 */
public class EmailSendingException extends RuntimeException {

    /**
     * Tworzy wyjątek z komunikatem o błędzie wysyłki e-mail.
     *
     * @param message szczegóły błędu
     */
    public EmailSendingException(String message) {
        super(message);
    }

    /**
     * Tworzy wyjątek z komunikatem i przyczyną ogólnego błędu.
     *
     * @param message szczegóły błędu
     * @param cause   przyczyna rzucenia wyjątku
     */
    public EmailSendingException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Tworzy wyjątek z komunikatem i przyczyną specyficzną dla MessagingException.
     *
     * @param message szczegóły błędu
     * @param cause   wyjątek MessagingException będący przyczyną
     */
    public EmailSendingException(String message, MessagingException cause) {
        super(message, cause);
    }
}
