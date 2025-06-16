package pl.zapala.system_obslugi_klienta.exception;

/**
 * Wyjątek sygnalizujący niedozwolony typ pliku w operacjach na plikach.
 * Rzucany, gdy plik nie spełnia oczekiwanego formatu (np. nie jest PDF).
 */
public class InvalidFileTypeException extends RuntimeException {

    /**
     * Tworzy wyjątek z komunikatem o niedozwolonym typie pliku.
     *
     * @param msg szczegóły błędu dotyczące typu pliku
     */
    public InvalidFileTypeException(String msg) {
        super(msg);
    }
}