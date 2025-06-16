package pl.zapala.system_obslugi_klienta.security;

import dev.samstevens.totp.exceptions.CodeGenerationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import pl.zapala.system_obslugi_klienta.exception.CodeGenerationFailedException;
import pl.zapala.system_obslugi_klienta.models.Pracownik;
import pl.zapala.system_obslugi_klienta.repositories.PracownikRepository;
import pl.zapala.system_obslugi_klienta.services.EmailService;

import java.io.IOException;
import java.time.Instant;

/**
 * Handler sukcesu uwierzytelnienia, inicjujący proces weryfikacji MFA.
 *
 * Po pomyślnym zalogowaniu ustawia w sesji dane potrzebne do MFA,
 * przekierowuje na stronę wprowadzania kodu oraz wysyła e-mail z kodem TOTP.
 */
@Component
public class MfaSuccessHandler implements AuthenticationSuccessHandler {

    private final PracownikRepository repo;
    private final TotpUtil totp;
    private final EmailService emailService;

    /**
     * Konstruktor handlera sukcesu uwierzytelnienia MFA.
     *
     * @param repo          repozytorium Pracownik służące do pobrania sekretu TOTP
     * @param totp          narzędzie do generowania kodów TOTP
     * @param emailService  serwis wysyłki e-maili z kodem TOTP
     */
    public MfaSuccessHandler(PracownikRepository repo,
                             TotpUtil totp,
                             EmailService emailService) {
        this.repo = repo;
        this.totp = totp;
        this.emailService = emailService;
    }

    /**
     * Metoda wywoływana po pomyślnym uwierzytelnieniu użytkownika.
     *
     * Inicjuje sesję MFA, zapisując adres e-mail i czas rozpoczęcia,
     * przekierowuje na stronę weryfikacji kodu oraz generuje i wysyła
     * kod TOTP na adres e-mail pracownika.
     *
     * @param req   obiekt żądania HTTP
     * @param res   obiekt odpowiedzi HTTP
     * @param auth  token uwierzytelnienia z nazwą użytkownika jako e-mail
     * @throws IOException gdy przekierowanie HTTP nie powiedzie się
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest req,
                                        HttpServletResponse res,
                                        Authentication auth) throws IOException {

        // Ustawienie atrybutów sesji dla procesu MFA
        HttpSession session = req.getSession();
        session.setAttribute("EMAIL", auth.getName());
        session.setAttribute("MFA_START", Instant.now().getEpochSecond());

        // Przekierowanie użytkownika do formularza MFA
        res.sendRedirect("/login?mfa");

        // Pobranie pracownika i wygenerowanie kodu TOTP
        Pracownik p = repo.findByEmail(auth.getName());
        String code = null;
        try {
            code = totp.generateCurrentCode(p.getTotpSecret());
        } catch (CodeGenerationException e) {
            throw new CodeGenerationFailedException("Nie udało się wygenerować kodu TOTP", e);
        }

        // Wysłanie kodu e-mail
        emailService.sendTotpEmail(p.getImie(), p.getEmail(), code);
    }
}