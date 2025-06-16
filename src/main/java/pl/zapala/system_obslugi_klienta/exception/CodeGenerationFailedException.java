package pl.zapala.system_obslugi_klienta.exception;

import dev.samstevens.totp.exceptions.CodeGenerationException;

/**
 * Wyjątek sygnalizujący niepowodzenie podczas generowania kodu TOTP.
 * Rzucany, gdy nie uda się utworzyć aktualnego kodu na podstawie sekretu.
 */
public class CodeGenerationFailedException extends RuntimeException {

    /**
     * Tworzy wyjątek z komunikatem o błędzie generowania kodu.
     *
     * @param message szczegóły błędu
     */
    public CodeGenerationFailedException(String message) {
        super(message);
    }

    /**
     * Tworzy wyjątek z komunikatem i przyczyną ogólnego wyjątku.
     *
     * @param message szczegóły błędu
     * @param cause   przyczyna rzucenia wyjątku
     */
    public CodeGenerationFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Tworzy wyjątek z komunikatem i przyczyną specyficzną dla błędu CodeGenerationException.
     *
     * @param message szczegóły błędu
     * @param cause   wyjątek CodeGenerationException będący przyczyną
     */
    public CodeGenerationFailedException(String message, CodeGenerationException cause) {
        super(message, cause);
    }
}
