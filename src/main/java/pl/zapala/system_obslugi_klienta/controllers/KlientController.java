package pl.zapala.system_obslugi_klienta.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.zapala.system_obslugi_klienta.repositories.RepozytoriumKlienta;

@Controller
@RequestMapping("/klienci")
public class KlientController {
    @Autowired
    private RepozytoriumKlienta klientRepo;

    @GetMapping({"","/"})
    public String getKlienci(Model model) {
        var klienci = klientRepo.findAll();
        model.addAttribute("klienci", klienci);

        return "klienci";
    }
}
