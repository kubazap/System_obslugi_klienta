package pl.zapala.system_obslugi_klienta.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.zapala.system_obslugi_klienta.repositories.RepozytoriumWizyty;

@Controller
@RequestMapping("/wizyty")
public class WizytaController {
    @Autowired
    private RepozytoriumWizyty wizytyRepo;

    @GetMapping({"","/"})
    public String getWizyty(Model model) {
        var wizyty = wizytyRepo.findAll();
        model.addAttribute("wizyty", wizyty);

        return "wizyty/index";
    }
}
