package pl.zapala.system_obslugi_klienta.controllers;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.zapala.system_obslugi_klienta.models.Pracownik;
import pl.zapala.system_obslugi_klienta.models.Wizyta;
import pl.zapala.system_obslugi_klienta.models.WizytaDto;
import pl.zapala.system_obslugi_klienta.repositories.PracownikRepository;
import pl.zapala.system_obslugi_klienta.repositories.KlientRepository;
import pl.zapala.system_obslugi_klienta.repositories.WizytaRepository;

/**
 * Kontroler zarządzający widokami i operacjami CRUD dla wizyt.
 * Umożliwia wyświetlanie listy wizyt, tworzenie, edycję oraz usuwanie.
 */
@Controller
@RequestMapping("/wizyty")
public class WizytaController {
    private static final Logger logger = LoggerFactory.getLogger(WizytaController.class);

    private static final String REDIRECT_WIZYTY = "redirect:/wizyty";
    private static final String REDIRECT_EDIT_WIZYTA = "wizyty/edytuj";
    private static final String REDIRECT_ADD_WIZYTA = "wizyty/dodaj";
    private static final String ATTR_KLIENCI = "klienci";
    private static final String ATTR_WIZYTA = "wizyta";
    private static final String ATTR_WIZYTA_DTO = "wizytaDto";

    private final WizytaRepository wizytyRepo;
    private final KlientRepository klientRepo;
    private final PracownikRepository pracownikRepo;

    /**
     * Tworzy instancję kontrolera wizyt.
     *
     * @param wizytyRepo   repozytorium obsługujące encje Wizyta
     * @param klientRepo   repozytorium obsługujące encje Klient
     * @param pracownikRepo repozytorium obsługujące encje Pracownik (dodawanie danych zalogowanego pracownika)
     */
    public WizytaController(WizytaRepository wizytyRepo,
                            KlientRepository klientRepo,
                            PracownikRepository pracownikRepo) {
        this.wizytyRepo   = wizytyRepo;
        this.klientRepo   = klientRepo;
        this.pracownikRepo = pracownikRepo;
    }

    /**
     * Dodaje do modelu zalogowanego pracownika, jeśli sesja jest uwierzytelniona.
     *
     * @param model model widoku, do którego trafia obiekt Pracownik
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
     * Wyświetla listę wszystkich wizyt.
     *
     * @param model model widoku, do którego trafia lista wizyt
     * @return nazwa widoku "wizyty/index"
     */
    @GetMapping({"","/"})
    public String getWizyty(Model model) {
        var wizyty = wizytyRepo.findAll();
        model.addAttribute("wizyty", wizyty);
        return "wizyty/index";
    }

    /**
     * Przygotowuje formularz do dodania nowej wizyty.
     *
     * @param model model widoku, do którego trafią lista klientów i pusty DTO wizyty
     * @return nazwa widoku "wizyty/dodaj"
     */
    @GetMapping("/dodaj")
    public String createWizyta(Model model) {
        var klienci = klientRepo.findAll();
        WizytaDto wizytaDto = new WizytaDto();
        model.addAttribute(ATTR_KLIENCI, klienci);
        model.addAttribute(ATTR_WIZYTA_DTO, wizytaDto);
        return REDIRECT_ADD_WIZYTA;
    }

    /**
     * Zapisuje nową wizytę na podstawie danych z formularza.
     * Waliduje poprawność DTO i loguje ewentualne błędy.
     *
     * @param wizytaDto obiekt DTO zawierający dane wizyty
     * @param result    obiekt przechowujący wyniki walidacji
     * @return przekierowanie na listę wizyt lub ponowne wyświetlenie formularza przy błędach
     */
    @PostMapping("/dodaj")
    public String createWizyta(@Valid @ModelAttribute WizytaDto wizytaDto, BindingResult result) {
        if (result.hasErrors()) {
            result.getAllErrors()
                    .forEach(err -> logger.warn("Błąd walidacji: {}", err));
            return REDIRECT_ADD_WIZYTA;
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
        return REDIRECT_WIZYTY;
    }

    /**
     * Przygotowuje formularz do edycji istniejącej wizyty.
     *
     * @param model model widoku, do którego trafiają dane wizyty, lista klientów i wypełniony DTO
     * @param id    identyfikator wizyty do edycji
     * @return nazwa widoku "wizyty/edytuj" lub przekierowanie na listę, gdy wizyty nie ma
     */
    @GetMapping("/edytuj")
    public String editWizyta(Model model, @RequestParam int id) {
        Wizyta wizyta = wizytyRepo.findById(id).orElse(null);
        if (wizyta == null) {
            return REDIRECT_WIZYTY;
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

        model.addAttribute(ATTR_KLIENCI, klienci);
        model.addAttribute(ATTR_WIZYTA, wizyta);
        model.addAttribute(ATTR_WIZYTA_DTO, wizytaDto);
        return REDIRECT_EDIT_WIZYTA;
    }

    /**
     * Zapisuje zmiany w edytowanej wizycie na podstawie DTO.
     * Waliduje poprawność DTO i aktualizuje encję.
     *
     * @param model     model widoku, do którego trafiają lista klientów i encja wizyty przy błędach
     * @param id        identyfikator wizyty do aktualizacji
     * @param wizytaDto obiekt DTO zawierający zaktualizowane dane wizyty
     * @param result    obiekt przechowujący wyniki walidacji
     * @return przekierowanie na listę wizyt lub ponowne wyświetlenie formularza przy błędach
     */
    @PostMapping("/edytuj")
    public String editWizyta(Model model, @RequestParam int id, @Valid @ModelAttribute WizytaDto wizytaDto, BindingResult result) {

        Wizyta wizyta = wizytyRepo.findById(id).orElse(null);
        if (wizyta == null) {
            return REDIRECT_WIZYTY;
        }

        if (result.hasErrors()) {
            var klienci = klientRepo.findAll();
            model.addAttribute(ATTR_KLIENCI, klienci);
            model.addAttribute(ATTR_WIZYTA, wizyta);
            return REDIRECT_EDIT_WIZYTA;
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
        return REDIRECT_WIZYTY;
    }

    /**
     * Usuwa wizytę o podanym identyfikatorze, jeśli istnieje.
     *
     * @param id identyfikator wizyty do usunięcia
     * @return przekierowanie na listę wizyt
     */
    @GetMapping("/usun")
    public String deleteWizyta(@RequestParam int id) {
        Wizyta wizyta = wizytyRepo.findById(id).orElse(null);
        if (wizyta != null) {
            wizytyRepo.delete(wizyta);
        }
        return REDIRECT_WIZYTY;
    }
}