package pl.zapala.system_obslugi_klienta.exception;
public class ControllerOperationException extends RuntimeException {
    public ControllerOperationException(String message) {
        super(message);
    }
    public ControllerOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
