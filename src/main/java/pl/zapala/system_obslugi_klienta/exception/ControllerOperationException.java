package pl.zapala.system_obslugi_klienta.exception;

/**
 * Wyjątek sygnalizujący błąd podczas wykonywania operacji w warstwie kontrolerów.
 * Rzucany gdy nie uda się zakończyć żądanej operacji HTTP w kontrolerze.
 */
public class ControllerOperationException extends RuntimeException {

    /**
     * Tworzy wyjątek z komunikatem o błędzie operacji w kontrolerze.
     *
     * @param message szczegóły błędu operacji
     */
    public ControllerOperationException(String message) {
        super(message);
    }

    /**
     * Tworzy wyjątek z komunikatem i przyczyną błędu operacji w kontrolerze.
     *
     * @param message szczegóły błędu operacji
     * @param cause   przyczyna rzucenia wyjątku
     */
    public ControllerOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
