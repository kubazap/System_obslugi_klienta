package pl.zapala.system_obslugi_klienta.controllers;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.zapala.system_obslugi_klienta.models.Pracownik;
import pl.zapala.system_obslugi_klienta.repositories.PracownikRepository;

/**
 * Kontroler obsługujący stronę czatu.
 * <p>
 * Ustawia zalogowanego pracownika jako model attribute przed wywołaniem endpointów,
 * a następnie udostępnia widok czatu.
 */
@Controller
@RequestMapping("/chat")
public class ChatController {

    private final PracownikRepository pracownikRepo;

    /**
     * Konstruktor inicjujący repozytorium pracowników.
     *
     * @param pracownikRepo repozytorium Pracownik do pobierania danych o pracownikach
     */
    public ChatController(PracownikRepository pracownikRepo) {
        this.pracownikRepo = pracownikRepo;
    }

    /**
     * Dodaje do modelu atrybut "pracownik" z obiektem zalogowanego pracownika,
     * jeśli użytkownik jest uwierzytelniony.
     * <p>
     * Wykonywane przed każdym obsługiwanym żądaniem w tym kontrolerze.
     *
     * @param model obiekt Model, do którego zostanie dodany pracownik
     */
    @ModelAttribute
    public void loggedPracownik(Model model) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()
                && !(auth instanceof AnonymousAuthenticationToken)) {

            String email = auth.getName();
            Pracownik pracownik = pracownikRepo.findByEmail(email);
            model.addAttribute("pracownik", pracownik);
        }
    }

    /**
     * Obsługuje żądanie GET do widoku czatu.
     *
     * @return nazwa widoku czatu (chat/index)
     */
    @GetMapping({"", "/"})
    public String getChat() {
        return "chat/index";
    }
}