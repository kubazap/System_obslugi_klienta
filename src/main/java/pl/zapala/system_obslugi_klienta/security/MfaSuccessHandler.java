package pl.zapala.system_obslugi_klienta.security;

import dev.samstevens.totp.exceptions.CodeGenerationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import pl.zapala.system_obslugi_klienta.models.Pracownik;
import pl.zapala.system_obslugi_klienta.repositories.PracownikRepository;
import pl.zapala.system_obslugi_klienta.services.EmailService;

import java.io.IOException;
import java.time.Instant;

@Component
public class MfaSuccessHandler implements AuthenticationSuccessHandler {

    private final PracownikRepository repo;
    private final TotpUtil totp;
    private final EmailService emailService;

    public MfaSuccessHandler(PracownikRepository repo,
                             TotpUtil totp,
                             EmailService emailService) {
        this.repo = repo;
        this.totp = totp;
        this.emailService = emailService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest req,
                                        HttpServletResponse res,
                                        Authentication auth) throws IOException {

        HttpSession session = req.getSession();
        session.setAttribute("EMAIL", auth.getName());
        session.setAttribute("MFA_START", Instant.now().getEpochSecond());

        res.sendRedirect("/login?mfa");

        Pracownik p = repo.findByEmail(auth.getName());
        String code = null;
        try {
            code = totp.generateCurrentCode(p.getTotpSecret());
        } catch (CodeGenerationException e) {
            throw new RuntimeException(e);
        }
        emailService.sendTotpEmail(p.getImie(), p.getEmail(), code);
    }
}

