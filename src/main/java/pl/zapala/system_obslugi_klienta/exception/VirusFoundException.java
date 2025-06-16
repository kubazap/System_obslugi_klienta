package pl.zapala.system_obslugi_klienta.exception;

/**
 * Wyjątek sygnalizujący wykrycie zainfekowanego pliku przez skaner antywirusowy.
 * Rzucany, gdy podczas skanowania pliku zostanie zidentyfikowany wirus.
 */
public class VirusFoundException extends RuntimeException {

    /**
     * Tworzy wyjątek z komunikatem informującym o wykryciu wirusa.
     *
     * @param msg szczegóły dotyczące wykrytego zagrożenia (np. nazwa sygnatury)
     */
    public VirusFoundException(String msg) {
        super(msg);
    }
}