package pl.zapala.system_obslugi_klienta.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Kontroler odpowiedzialny za wyświetlanie strony logowania pracowników.
 */
@Controller
public class PracownikController {

    /**
     * Obsługuje żądanie GET na ścieżkę "/login" i zwraca widok formularza logowania.
     *
     * @return nazwa szablonu widoku logowania ("logowanie/login")
     */
    @GetMapping("/login")
    public String login() {
        return "logowanie/login";
    }
}
