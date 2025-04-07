package pl.zapala.system_obslugi_klienta.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import pl.zapala.system_obslugi_klienta.models.Wizyta;
import pl.zapala.system_obslugi_klienta.models.WizytaDto;
import pl.zapala.system_obslugi_klienta.repositories.RepozytoriumWizyty;
import pl.zapala.system_obslugi_klienta.repositories.RepozytoriumKlienta;

@Controller
@RequestMapping("/wizyty")
public class WizytaController {
    @Autowired
    private RepozytoriumWizyty wizytyRepo;
    @Autowired
    private RepozytoriumKlienta klientRepo;

    @GetMapping({"","/"})
    public String getWizyty(Model model) {
        var wizyty = wizytyRepo.findAll();
        model.addAttribute("wizyty", wizyty);

        return "wizyty/index";
    }

    @GetMapping("/dodaj")
    public String createWizyta(Model model) {
        var klienci = klientRepo.findAll();

        WizytaDto wizytaDto = new WizytaDto();

        model.addAttribute("klienci", klienci);
        model.addAttribute("wizytaDto", wizytaDto);

        return "wizyty/dodaj";
    }

    @PostMapping("/dodaj")
    public String createWizyta(@Valid @ModelAttribute WizytaDto wizytaDto, BindingResult result) {

        if(result.hasErrors()) {
            result.getAllErrors().forEach(err -> System.out.println(err.toString()));
            return "wizyty/dodaj";
        }

        Wizyta wizyta = new Wizyta();
        wizyta.setDataWizyty(wizytaDto.getDataWizyty());
        wizyta.setGodzina(wizytaDto.getGodzina());
        wizyta.setPokoj(wizytaDto.getPokoj());
        wizyta.setCzyOplacona(wizytaDto.getCzyOplacona());
        wizyta.setNaleznosc(wizytaDto.getNaleznosc());
        wizyta.setSposobPlatnosci(wizytaDto.getSposobPlatnosci());
        wizyta.setUwagi(wizytaDto.getUwagi());

        if (wizytaDto.getKlientId() != null) {
            var klientOpt = klientRepo.findById(wizytaDto.getKlientId());
            if (klientOpt.isPresent()) {
                wizyta.setKlient(klientOpt.get());
            }
        }

        wizytyRepo.save(wizyta);

        return "redirect:/wizyty";
    }

    @GetMapping("/edytuj")
    public String editWizyta(Model model, @RequestParam int id) {

        Wizyta wizyta = wizytyRepo.findById(id).orElse(null);
        if (wizyta == null) {
            return "redirect:/wizyty";
        }

        var klienci = klientRepo.findAll();

        WizytaDto wizytaDto = new WizytaDto();
        wizytaDto.setDataWizyty(wizyta.getDataWizyty());
        wizytaDto.setGodzina(wizyta.getGodzina());
        wizytaDto.setPokoj(wizyta.getPokoj());
        wizytaDto.setCzyOplacona(wizyta.getCzyOplacona());
        wizytaDto.setNaleznosc(wizyta.getNaleznosc());
        wizytaDto.setSposobPlatnosci(wizyta.getSposobPlatnosci());
        wizytaDto.setUwagi(wizyta.getUwagi());

        if (wizyta.getKlient() != null) {
            wizytaDto.setKlientId(wizyta.getKlient().getId());
        }

        model.addAttribute("klienci", klienci);
        model.addAttribute("wizyta", wizyta);
        model.addAttribute("wizytaDto", wizytaDto);

        return "wizyty/edytuj";
    }

    @PostMapping("/edytuj")
    public String editWizyta(Model model, @RequestParam int id, @Valid @ModelAttribute WizytaDto wizytaDto, BindingResult result) {

        Wizyta wizyta = wizytyRepo.findById(id).orElse(null);
        if (wizyta == null) {
            return "redirect:/wizyty";
        }

        if (result.hasErrors()) {
            var klienci = klientRepo.findAll();
            model.addAttribute("klienci", klienci);
            model.addAttribute("wizyta", wizyta);

            return "wizyty/edytuj";
        }

        wizyta.setDataWizyty(wizytaDto.getDataWizyty());
        wizyta.setGodzina(wizytaDto.getGodzina());
        wizyta.setPokoj(wizytaDto.getPokoj());
        wizyta.setCzyOplacona(wizytaDto.getCzyOplacona());
        wizyta.setNaleznosc(wizytaDto.getNaleznosc());
        wizyta.setSposobPlatnosci(wizytaDto.getSposobPlatnosci());
        wizyta.setUwagi(wizytaDto.getUwagi());

        if (wizytaDto.getKlientId() != null) {
            var klientOpt = klientRepo.findById(wizytaDto.getKlientId());
            klientOpt.ifPresent(wizyta::setKlient);
        } else {
            wizyta.setKlient(null);
        }

        wizytyRepo.save(wizyta);

        return "redirect:/wizyty";
    }

    @GetMapping("/usun")
    public String deleteWizyta(@RequestParam int id) {

        Wizyta wizyta = wizytyRepo.findById(id).orElse(null);

        if(wizyta != null) {
            wizytyRepo.delete(wizyta);
        }

        return "redirect:/wizyty";
    }
}
