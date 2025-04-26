package pl.zapala.system_obslugi_klienta.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PracownikController {

    @GetMapping("/login")
    public String login() {
        return "logowanie/login";
    }
}
