package pl.zapala.system_obslugi_klienta.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.zapala.system_obslugi_klienta.models.Pracownik;
import pl.zapala.system_obslugi_klienta.repositories.PracownikRepository;

@Controller
@RequestMapping("/chat")
public class ChatController {
    @Autowired
    private PracownikRepository pracownikRepo;

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

    @GetMapping({"", "/"})
    public String getChat() {

        return "chat/index";
    }
}
