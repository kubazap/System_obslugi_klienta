package pl.zapala.system_obslugi_klienta;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import pl.zapala.system_obslugi_klienta.controllers.KlientController;
import pl.zapala.system_obslugi_klienta.models.*;
import pl.zapala.system_obslugi_klienta.repositories.*;
import pl.zapala.system_obslugi_klienta.controllers.WizytaController;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.mail.username=dummy",
        "spring.mail.password=dummy",
        "spring.mail.host=localhost",
        "emails.sender_email=dummy@example.com"})
public class WizytaControllerTest {

    @Autowired
    private KlientController klientController;

    @Autowired
    private WizytaController wizytaController;

    @Autowired
    private KlientRepository klientRepository;

    @Autowired
    private WizytaRepository wizytaRepository;

    private KlientDto klientDto;

    private WizytaDto wizytaDto;

    private Wizyta wizyta;
    private Klient klient;

    @BeforeEach
    void setUp() {
        klientRepository.deleteAll();
        wizytaRepository.deleteAll();

        klient = new Klient();
        klient.setImie("Anna");
        klient.setNazwisko("Kowalska");
        klient.setDataUrodzenia(Date.valueOf(LocalDate.of(1995, 5, 10)));
        klient.setEmail("anna@example.com");
        klient.setKodPocztowy("00-001");
        klient.setUlicaNumerDomu("ul. Kwiatowa 12");
        klientRepository.save(klient);

        wizyta = new Wizyta();
        wizyta.setDataWizyty(Date.valueOf(LocalDate.now()));
        wizyta.setGodzina("10.00");
        wizyta.setPokoj("101");
        wizyta.setCzyOplacona(false);
        wizyta.setNaleznosc("100 zł");
        wizyta.setSposobPlatnosci("gotówka");
        wizyta.setKlient(klient);
        wizytaRepository.save(wizyta);
    }

    @Nested
    @DisplayName("Poprawne dodanie klienta")
    class KlientValidation {


        @DisplayName("Authentykacja - loggedPracownik")
        @Test
        void loggedPracownik_shouldAddLoggedUserToModel_whenAuthenticated() {
            // Mock Authentication
            Authentication authentication = mock(Authentication.class);
            when(authentication.isAuthenticated()).thenReturn(true);
            when(authentication.getName()).thenReturn("jan@example.com");

            // Ustaw SecurityContext
            SecurityContext securityContext = mock(SecurityContext.class);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            SecurityContextHolder.setContext(securityContext);

            // Mock PracownikRepository
            PracownikRepository pracownikRepo = mock(PracownikRepository.class);
            Pracownik expectedPracownik = new Pracownik();
            expectedPracownik.setEmail("jan@example.com");
            when(pracownikRepo.findByEmail("jan@example.com")).thenReturn(expectedPracownik);

            // Mock Model
            Model model = mock(Model.class);

            // Stwórz instancję kontrolera
            WizytaController controller = new WizytaController(
                    mock(WizytaRepository.class),
                    mock(KlientRepository.class),
                    pracownikRepo);

            // Wywołaj metodę
            controller.loggedPracownik(model);

            // Sprawdź, czy pracownik został dodany do modelu
            verify(model).addAttribute("pracownik", expectedPracownik);
        }

        @Test
        @DisplayName("Pobranie wizyt(model)")
        void shouldGetWizyty() {
            ExtendedModelMap model = new ExtendedModelMap();
            String viewName = wizytaController.getWizyty(model);
            assertEquals("wizyty/index", viewName);
        }

        @Test
        @DisplayName("Stworzenie wizyty(model)")
        void CreateWizytaModel() {
            ExtendedModelMap model = new ExtendedModelMap();
            String viewName = wizytaController.createWizyta(model);
            assertEquals("wizyty/dodaj", viewName);
        }

        @Test
        @DisplayName("Stworzenie wizyty - poprawne")
        void CreateWizytaSuccess() {
            BindingResult result = mock(BindingResult.class);

            wizytaDto = new WizytaDto();
            wizytaDto.setGodzina("10.00");
            wizytaDto.setPokoj("12 C");
            wizytaDto.setNaleznosc("182 zł");
            wizytaDto.setKlientId(klient.getId());
            String viewName = wizytaController.createWizyta(wizytaDto, result);
            List<Wizyta> Wizyty = wizytaRepository.findAll();
            assertEquals("redirect:/wizyty", viewName);
            assertEquals("10.00",Wizyty.get(1).getGodzina());
            assertEquals("182 zł",Wizyty.get(1).getNaleznosc());
        }
        @Test
        @DisplayName("Stworzenie wizyty - bez klienta")
        void CreateWizytaNoKlient() {
            BindingResult result = mock(BindingResult.class);
            wizytaDto = new WizytaDto();
            wizytaDto.setGodzina("10.00");
            wizytaDto.setPokoj("12 C");
            wizytaDto.setNaleznosc("182 zł");
            String viewName = wizytaController.createWizyta(wizytaDto,result);
            List<Wizyta> Wizyty = wizytaRepository.findAll();

            assertEquals("redirect:/wizyty", viewName);
            assertEquals(null, Wizyty.get(1).getKlient());
        }

        @Test
        @DisplayName("Stworzenie wizyty - bez klienta, ale z id")
        void CreateWizytaNoKlientButWithId() {
            BindingResult result = mock(BindingResult.class);
            wizytaDto = new WizytaDto();
            wizytaDto.setGodzina("10.00");
            wizytaDto.setPokoj("12 C");
            wizytaDto.setNaleznosc("182 zł");
            wizytaDto.setKlientId(1233);
            String viewName = wizytaController.createWizyta(wizytaDto,result);
            List<Wizyta> Wizyty = wizytaRepository.findAll();

            assertEquals("redirect:/wizyty", viewName);
            assertEquals(null, Wizyty.get(1).getKlient());
        }
        @Test
        @DisplayName("Stworzenie wizyty - Errors")
        void CreateWizytaWithErrors() {
            BindingResult result = mock(BindingResult.class);
            when(result.hasErrors()).thenReturn(true);

            wizytaDto = new WizytaDto();
            String viewName = wizytaController.createWizyta(wizytaDto, result);
            assertEquals("wizyty/dodaj", viewName);
        }

        @Test
        @DisplayName("Edycja wizyty(model) - poprawne")
        void shoulEditWizytaNotNull() {

            ExtendedModelMap model = new ExtendedModelMap();
            String viewName = wizytaController.editWizyta(model,wizyta.getId());
            List<Wizyta> Wizyty = wizytaRepository.findAll();

            assertEquals("wizyty/edytuj", viewName);
            assertEquals("100 zł", Wizyty.get(0).getNaleznosc());
            assertTrue(Wizyty.get(0).getKlient()!=null);
        }
        @Test
        @DisplayName("Edycja wizyty(model) - brak wizyty")
        void shoulEditWizyta() {
            wizytaDto = new WizytaDto();
            ExtendedModelMap model = new ExtendedModelMap();
            String viewName = wizytaController.editWizyta(model,11);
            assertEquals("redirect:/wizyty", viewName);
        }

        @Test
        @DisplayName("Edycja wizyty(model) - bez klienta")
        void shoulEditWizytaNotNullKlientNull() {
            ExtendedModelMap model = new ExtendedModelMap();
            wizyta.setKlient(null);
            wizytaRepository.save(wizyta);
            String viewName = wizytaController.editWizyta(model,wizyta.getId());
            List<Wizyta> Wizyty = wizytaRepository.findAll();

            assertEquals("wizyty/edytuj", viewName);
            assertEquals("100 zł", Wizyty.get(0).getNaleznosc());
            assertTrue(Wizyty.get(0).getKlient()==null);
        }

        @Test
        @DisplayName("Edycja wizyty - poprawna")
        void shoulEditWizyta2NotNullKlientNull() {
            ExtendedModelMap model = new ExtendedModelMap();
            BindingResult result = mock(BindingResult.class);

            WizytaDto wizytaDto = new WizytaDto();
            wizytaDto.setDataWizyty(Date.valueOf(LocalDate.now()));
            wizytaDto.setGodzina("11.00");
            wizytaDto.setPokoj("101 A");
            wizytaDto.setCzyOplacona(false);
            wizytaDto.setNaleznosc("200 zł");
            wizytaDto.setSposobPlatnosci("gotówka");
            wizytaDto.setKlientId(wizyta.getKlient().getId());
            String viewName =  wizytaController.editWizyta(model,wizyta.getId(),wizytaDto,result);
            List<Wizyta> Wizyty = wizytaRepository.findAll();
            assertEquals("redirect:/wizyty", viewName);
            assertEquals("200 zł", Wizyty.get(0).getNaleznosc());
            assertFalse(Wizyty.get(0).getCzyOplacona());
            assertTrue(Wizyty.get(0).getKlient()!=null);
        }

        @Test
        @DisplayName("Edycja wizyty - brak wizyty")
        void shoulEditWizyta2() {
            ExtendedModelMap model = new ExtendedModelMap();
            BindingResult result = mock(BindingResult.class);

            WizytaDto wizytaDto = new WizytaDto();
            String viewName =  wizytaController.editWizyta(model,123,wizytaDto,result);
            List<Wizyta> Wizyty = wizytaRepository.findAll();
            assertEquals("redirect:/wizyty", viewName);
        }

        @Test
        @DisplayName("Edycja wizyty - Errors")
        void shoulEditWizyta2NotNullHasErrors() {
            ExtendedModelMap model = new ExtendedModelMap();
            BindingResult result = mock(BindingResult.class);
            when(result.hasErrors()).thenReturn(true);

            WizytaDto wizytaDto = new WizytaDto();
            String viewName =  wizytaController.editWizyta(model,wizyta.getId(),wizytaDto,result);
            List<Wizyta> Wizyty = wizytaRepository.findAll();
            assertEquals("wizyty/edytuj", viewName);
        }

        @Test
        @DisplayName("Edycja wizyty - brak klienta")
        void shoulEditWizyta2NotNull() {
            ExtendedModelMap model = new ExtendedModelMap();
            BindingResult result = mock(BindingResult.class);

            WizytaDto wizytaDto = new WizytaDto();
            wizytaDto.setDataWizyty(Date.valueOf(LocalDate.now()));
            wizytaDto.setGodzina("11.00");
            wizytaDto.setPokoj("101 A");
            wizytaDto.setCzyOplacona(false);
            wizytaDto.setNaleznosc("200 zł");
            wizytaDto.setSposobPlatnosci("gotówka");
            String viewName =  wizytaController.editWizyta(model,wizyta.getId(),wizytaDto,result);
            List<Wizyta> Wizyty = wizytaRepository.findAll();
            assertEquals("redirect:/wizyty", viewName);
            assertEquals("200 zł", Wizyty.get(0).getNaleznosc());
            assertFalse(Wizyty.get(0).getCzyOplacona());
            assertTrue(Wizyty.get(0).getKlient()==null);
        }

        @Test
        @DisplayName("Usuń wizyte - poprawnie")
        void shoulDeleteWizyta() {
            String viewName =  wizytaController.deleteWizyta(wizyta.getId());
            List<Wizyta> Wizyty = wizytaRepository.findAll();
            assertTrue(Wizyty.isEmpty());
        }

        @Test
        @DisplayName("Usuwa wizytę - nie znaleziono wizyty")
        void shouldDeleteWizyta2() {
            String viewName =  wizytaController.deleteWizyta(123);
            List<Wizyta> Wizyty = wizytaRepository.findAll();
            assertFalse(Wizyty.isEmpty());
        }
    }
}
