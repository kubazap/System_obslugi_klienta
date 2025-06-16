package pl.zapala.system_obslugi_klienta.exception;

/**
 * Wyjątek sygnalizujący niepowodzenie podczas weryfikacji kodu MFA (TOTP).
 * Rzucany, gdy kod jest nieprawidłowy lub sesja MFA wygasła.
 */
public class MfaVerificationException extends RuntimeException {

    /**
     * Tworzy wyjątek z komunikatem o błędzie weryfikacji MFA.
     *
     * @param message szczegóły błędu weryfikacji
     */
    public MfaVerificationException(String message) {
        super(message);
    }

    /**
     * Tworzy wyjątek z komunikatem oraz przyczyną błędu weryfikacji MFA.
     *
     * @param message szczegóły błędu weryfikacji
     * @param cause   przyczyna rzucenia wyjątku
     */
    public MfaVerificationException(String message, Throwable cause) {
        super(message, cause);
    }
}