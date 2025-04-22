package pl.zapala.system_obslugi_klienta.controllers;

import dev.samstevens.totp.code.CodeVerifier;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.zapala.system_obslugi_klienta.repositories.PracownikRepository;

import java.time.Instant;

@Controller
public class MfaController {

    private final CodeVerifier verifier;
    private final PracownikRepository repo;

    public MfaController(CodeVerifier verifier, PracownikRepository repo) {
        this.verifier = verifier;
        this.repo = repo;
    }

    @GetMapping(value = "/login", params = "mfa")
    public String mfaForm(HttpSession session,
                          Model model,
                          @RequestParam(required = false) String error) {

        Long start = (Long) session.getAttribute("MFA_START");
        if (start == null) {
            return "redirect:/login?timeout";
        }

        long now = Instant.now().getEpochSecond();
        long elapsed = now - start;
        long remaining = 120 - elapsed;

        if (remaining <= 0) {
            session.removeAttribute("MFA_START");
            return "redirect:/login?timeout";
        }

        model.addAttribute("remainingSeconds", remaining);
        if (error != null) model.addAttribute("error", true);
        return "logowanie/mfa";
    }

    @PostMapping(value = "/login", params = "mfa")
    public String verify(@RequestParam String code, HttpSession session) {
        String email = (String) session.getAttribute("EMAIL");
        if (email == null) {
            return "redirect:/login?timeout";
        }

        String secret = repo.findByEmail(email).getTotpSecret();
        if (verifier.isValidCode(secret, code)) {
            session.setAttribute("MFA_PASSED", Boolean.TRUE);
            return "redirect:/wizyty";
        }
        return "redirect:/login?mfa&error";
    }
}
