package pl.zapala.system_obslugi_klienta.exception;
public class MfaVerificationException extends RuntimeException {
    public MfaVerificationException(String message) {
        super(message);
    }
    public MfaVerificationException(String message, Throwable cause) {
        super(message, cause);
    }
}