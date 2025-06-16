package pl.zapala.system_obslugi_klienta.controllers;

import dev.samstevens.totp.code.CodeVerifier;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.zapala.system_obslugi_klienta.repositories.PracownikRepository;
import pl.zapala.system_obslugi_klienta.exception.MfaVerificationException;

import java.time.Instant;

/**
 * Kontroler obsługujący proces weryfikacji wieloskładnikowego uwierzytelniania (MFA).
 * Udostępnia formularz do wprowadzenia kodu TOTP oraz przetwarza wysłany kod.
 */
@Controller
public class MfaController {

    private final CodeVerifier verifier;
    private final PracownikRepository repo;

    /**
     * Tworzy instancję kontrolera MFA.
     *
     * @param verifier komponent weryfikujący poprawność kodu TOTP
     * @param repo     repozytorium dostępu do encji Pracownik (zawierającej sekret TOTP)
     */
    public MfaController(CodeVerifier verifier, PracownikRepository repo) {
        this.verifier = verifier;
        this.repo     = repo;
    }

    /**
     * Wyświetla formularz weryfikacji TOTP po udanym uwierzytelnieniu hasłem.
     * Sprawdza, czy sesja MFA jest nadal ważna, i oblicza pozostały czas na wpisanie kodu.
     *
     * @param session obiekt sesji HTTP, z atrybutem "MFA_START" przechowującym czas rozpoczęcia
     * @param model   model widoku, do którego trafia informacja o pozostałych sekundach i błędzie
     * @param error   opcjonalny parametr sygnalizujący błąd poprzedniej próby weryfikacji
     * @return nazwa widoku formularza MFA ("logowanie/mfa")
     * @throws MfaVerificationException gdy sesja MFA wygasła lub nie została zainicjowana
     */
    @GetMapping(value = "/login", params = "mfa")
    public String mfaForm(HttpSession session,
                          Model model,
                          @RequestParam(required = false) String error) {

        Long start = (Long) session.getAttribute("MFA_START");
        if (start == null) {
            throw new MfaVerificationException("Sesja wygasła – zaloguj się ponownie.");
        }

        long now       = Instant.now().getEpochSecond();
        long elapsed   = now - start;
        long remaining = 120 - elapsed;

        if (remaining <= 0) {
            session.removeAttribute("MFA_START");
            throw new MfaVerificationException("Sesja wygasła – zaloguj się ponownie.");
        }

        model.addAttribute("remainingSeconds", remaining);
        if (error != null) {
            model.addAttribute("error", true);
        }
        return "logowanie/mfa";
    }

    /**
     * Przetwarza wprowadzony kod TOTP, weryfikuje go i oznacza sesję jako przejście MFA.
     *
     * @param code    kod TOTP wpisany przez użytkownika
     * @param session obiekt sesji HTTP, z atrybutami "EMAIL" i "MFA_START"
     * @return przekierowanie do panelu wizyt po pomyślnej weryfikacji ("redirect:/wizyty")
     * @throws MfaVerificationException gdy sesja wygasła, nie ma emaila lub kod jest nieprawidłowy
     */
    @PostMapping(value = "/login", params = "mfa")
    public String verify(@RequestParam String code, HttpSession session) {
        String email = (String) session.getAttribute("EMAIL");
        if (email == null) {
            throw new MfaVerificationException("Sesja wygasła – zaloguj się ponownie.");
        }

        String secret = repo.findByEmail(email).getTotpSecret();
        if (!verifier.isValidCode(secret, code)) {
            throw new MfaVerificationException("Nieprawidłowy kod weryfikacyjny.");
        }

        session.setAttribute("MFA_PASSED", Boolean.TRUE);
        return "redirect:/wizyty";
    }
}
