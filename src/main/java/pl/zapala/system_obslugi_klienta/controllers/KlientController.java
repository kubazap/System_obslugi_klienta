package pl.zapala.system_obslugi_klienta.controllers;

import jakarta.validation.Valid;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import pl.zapala.system_obslugi_klienta.models.Klient;
import pl.zapala.system_obslugi_klienta.models.KlientDto;
import pl.zapala.system_obslugi_klienta.models.Pracownik;
import pl.zapala.system_obslugi_klienta.repositories.KlientRepository;
import pl.zapala.system_obslugi_klienta.repositories.PracownikRepository;

@Controller
@RequestMapping("/klienci")
public class KlientController {
    private static final String REDIRECT_KLIENCI = "redirect:/klienci";
    private static final String REDIRECT_EDIT_KLIENT = "klienci/edytuj";
    private static final String REDIRECT_ADD_KLIENT = "klienci/dodaj";
    private static final String ATTR_KLIENT = "klient";
    private static final String ATTR_KLIENT_DTO = "klientDto";

    private final KlientRepository klientRepo;
    private final PracownikRepository pracownikRepo;

    public KlientController(KlientRepository klientRepo,
                            PracownikRepository pracownikRepo) {
        this.klientRepo    = klientRepo;
        this.pracownikRepo = pracownikRepo;
    }

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

    @GetMapping({"","/"})
    public String getKlienci(Model model) {
        var klienci = klientRepo.findAll();
        model.addAttribute("klienci", klienci);

        return "klienci/index";
    }

    @GetMapping("/dodaj")
    public String createKlient(Model model) {
        KlientDto klientDto = new KlientDto();
        model.addAttribute(ATTR_KLIENT_DTO, klientDto);

        return REDIRECT_ADD_KLIENT;
    }

    @PostMapping("/dodaj")
    public String createKlient(@Valid @ModelAttribute KlientDto klientDto, BindingResult result) {

        if(klientRepo.findByEmail(klientDto.getEmail()) != null) {
            result.addError(
                    new FieldError(ATTR_KLIENT_DTO, "email", klientDto.getEmail(),
                            false, null, null, "Klient o takim adresie e-mail już istnieje.")
            );
        }

        if(result.hasErrors()) {
            return REDIRECT_ADD_KLIENT;
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

        return REDIRECT_KLIENCI;
    }

    @GetMapping("/edytuj")
    public String editKlient(Model model, @RequestParam int id) {

        Klient klient = klientRepo.findById(id).orElse(null);

        if(klient == null) {
            return REDIRECT_KLIENCI;
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

        model.addAttribute(ATTR_KLIENT, klient);
        model.addAttribute(ATTR_KLIENT_DTO, klientDto);

        return REDIRECT_EDIT_KLIENT;
    }

    @PostMapping("/edytuj")
    public String editKlient(Model model, @RequestParam int id, @Valid @ModelAttribute KlientDto klientDto, BindingResult result) {

        Klient klient = klientRepo.findById(id).orElse(null);

        if(klient == null) {
            return REDIRECT_KLIENCI;
        }

        model.addAttribute(ATTR_KLIENT, klient);

        if(result.hasErrors()) {
            return REDIRECT_EDIT_KLIENT;
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
                    new FieldError(ATTR_KLIENT_DTO, "email", klientDto.getEmail(),
                            false, null, null, "Klient o takim adresie e-mail już istnieje.")
            );

            return REDIRECT_EDIT_KLIENT;
        }

        return REDIRECT_KLIENCI;
    }

    @GetMapping("/usun")
    public String deleteKlient(@RequestParam int id) {

        Klient klient = klientRepo.findById(id).orElse(null);

        if(klient != null) {
            klientRepo.delete(klient);
        }

        return REDIRECT_KLIENCI;
    }
}
