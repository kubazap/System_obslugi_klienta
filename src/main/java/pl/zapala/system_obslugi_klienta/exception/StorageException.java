package pl.zapala.system_obslugi_klienta.exception;

/**
 * Wyjątek sygnalizujący problemy z operacjami na zasobach magazynowania plików.
 * Rzucany w przypadku błędów odczytu, zapisu lub połączenia z serwisem skanującym pliki.
 */
public class StorageException extends RuntimeException {

    /**
     * Tworzy wyjątek z komunikatem o błędzie magazynowania.
     *
     * @param message szczegóły błędu operacji magazynowania
     */
    public StorageException(String message) {
        super(message);
    }

    /**
     * Tworzy wyjątek z komunikatem oraz przyczyną błędu magazynowania.
     *
     * @param message szczegóły błędu operacji magazynowania
     * @param cause   przyczyna rzucenia wyjątku
     */
    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
