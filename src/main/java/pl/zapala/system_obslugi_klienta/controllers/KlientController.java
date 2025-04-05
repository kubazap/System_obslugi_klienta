package pl.zapala.system_obslugi_klienta.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import pl.zapala.system_obslugi_klienta.models.Klient;
import pl.zapala.system_obslugi_klienta.models.KlientDto;
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

        return "klienci/index";
    }

    @GetMapping("/dodaj")
    public String createKlient(Model model) {
        KlientDto klientDto = new KlientDto();
        model.addAttribute("klientDto", klientDto);

        return "klienci/dodaj";
    }

    @PostMapping("/dodaj")
    public String createKlient(@Valid @ModelAttribute KlientDto klientDto, BindingResult result) {

        if(klientRepo.findByEmail(klientDto.getEmail()) != null) {
            result.addError(
                    new FieldError("klientDto", "email", klientDto.getEmail(),
                            false, null, null, "Klient o takim adresie e-mail już istnieje.")
            );
        }

        if(result.hasErrors()) {
            return "klienci/dodaj";
        }

        Klient klient = new Klient();
        klient.setImie(klientDto.getImie());
        klient.setNazwisko(klientDto.getNazwisko());
        klient.setDataUrodzenia(klientDto.getDataUrodzenia());
        klient.setUlicaNumerDomu(klientDto.getUlicaNumerDomu());
        klient.setKodPocztowy(klientDto.getKodPocztowy());
        klient.setMiejscowosc(klientDto.getMiejscowosc());
        klient.setEmail(klientDto.getEmail());
        klient.setNumerTelefonu(klientDto.getNumerTelefonu());

        klientRepo.save(klient);

        return "redirect:/klienci";
    }

    @GetMapping("/edytuj")
    public String editKlient(Model model, @RequestParam int id) {

        Klient klient = klientRepo.findById(id).orElse(null);

        if(klient == null) {
            return "redirect:/klienci";
        }

        KlientDto klientDto = new KlientDto();
        klientDto.setImie(klient.getImie());
        klientDto.setNazwisko(klient.getNazwisko());
        klientDto.setDataUrodzenia(klient.getDataUrodzenia());
        klientDto.setUlicaNumerDomu(klient.getUlicaNumerDomu());
        klientDto.setKodPocztowy(klient.getKodPocztowy());
        klientDto.setMiejscowosc(klient.getMiejscowosc());
        klientDto.setEmail(klient.getEmail());
        klientDto.setNumerTelefonu(klient.getNumerTelefonu());

        model.addAttribute("klient", klient);
        model.addAttribute("klientDto", klientDto);

        return "klienci/edytuj";
    }

    @PostMapping("/edytuj")
    public String editKlient(Model model, @RequestParam int id, @Valid @ModelAttribute KlientDto klientDto, BindingResult result) {

        Klient klient = klientRepo.findById(id).orElse(null);

        if(klient == null) {
            return "redirect:/klienci";
        }

        model.addAttribute("klient", klient);

        if(result.hasErrors()) {
            return "klienci/edytuj";
        }

        klient.setImie(klientDto.getImie());
        klient.setNazwisko(klientDto.getNazwisko());
        klient.setDataUrodzenia(klientDto.getDataUrodzenia());
        klient.setUlicaNumerDomu(klientDto.getUlicaNumerDomu());
        klient.setKodPocztowy(klientDto.getKodPocztowy());
        klient.setMiejscowosc(klientDto.getMiejscowosc());
        klient.setEmail(klientDto.getEmail());
        klient.setNumerTelefonu(klientDto.getNumerTelefonu());

        try {
            klientRepo.save(klient);
        }
        catch(Exception ex) {
            result.addError(
                    new FieldError("klientDto", "email", klientDto.getEmail(),
                            false, null, null, "Klient o takim adresie e-mail już istnieje.")
            );

            return "klienci/edytuj";
        }

        return "redirect:/klienci";
    }

    @GetMapping("/usun")
    public String deleteKlient(@RequestParam int id) {

        Klient klient = klientRepo.findById(id).orElse(null);

        if(klient != null) {
            klientRepo.delete(klient);
        }

        return "redirect:/klienci";
    }
}
