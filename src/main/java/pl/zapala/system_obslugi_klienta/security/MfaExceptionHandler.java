package pl.zapala.system_obslugi_klienta.security;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.zapala.system_obslugi_klienta.exception.MfaVerificationException;

/**
 * Globalny handler wyjątków związanych z weryfikacją MFA.
 *
 * Obsługuje wyjątki MfaVerificationException i przekierowuje użytkownika
 * na stronę logowania z odpowiednimi parametrami zapytania oraz komunikatem
 * o błędzie.
 */
@ControllerAdvice
public class MfaExceptionHandler {

    /**
     * Przechwytuje wyjątek MfaVerificationException, dodaje wiadomość do atrybutów
     * i zwraca odpowiedni adres przekierowania.
     *
     * @param ex    wyjątek zawierający szczegóły błędu weryfikacji MFA
     * @param attrs atrybuty przekierowania pozwalające przekazać komunikat o błędzie
     * @return ścieżka do przekierowania użytkownika na stronę logowania z parametrami
     */
    @ExceptionHandler(MfaVerificationException.class)
    public String handleMfaException(MfaVerificationException ex,
                                     RedirectAttributes attrs) {
        String msg = ex.getMessage();
        attrs.addFlashAttribute("mfaErrorMessage", msg);

        // Jeżeli sesja wygasła, dodaj parametr timeout
        if (msg.contains("Sesja wygasła")) {
            return "redirect:/login?timeout";
        }
        // W przeciwnym razie dodaj parametr mfa i error
        return "redirect:/login?mfa&error";
    }
}
