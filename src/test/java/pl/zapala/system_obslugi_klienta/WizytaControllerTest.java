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

    @BeforeEach
    void setUp() {
        klientRepository.deleteAll();
        wizytaRepository.deleteAll();

        klientDto = new KlientDto();
        klientDto.setImie("Anna");
        klientDto.setNazwisko("Kowalska");
        klientDto.setDataUrodzenia(Date.valueOf(LocalDate.of(1995, 5, 10)));
        klientDto.setEmail("anna@example.com");
        klientDto.setKodPocztowy("00-001");
        klientDto.setUlicaNumerDomu("ul. Kwiatowa 12");
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
        @DisplayName("Pobranie wizyt")
        void shouldGetWizyty() {
            ExtendedModelMap model = new ExtendedModelMap();
            wizytaController.getWizyty(model);
        }

        @Test
        @DisplayName("Stworzenie wizyty")
        void shouldCreateWizyta() {
            ExtendedModelMap model = new ExtendedModelMap();
            wizytaController.createWizyta(model);
        }

        @Test
        @DisplayName("Stworzenie wizyty2")
        void shouldCreateWizyta2() {
            BindingResult result = mock(BindingResult.class);
            WizytaDto wizytaDto = new WizytaDto();
            wizytaController.createWizyta(wizytaDto,result);
        }
        @Test
        @DisplayName("Stworzenie wizyty z Errors")
        void shouldCreateWizytaWithError() {
            BindingResult result = mock(BindingResult.class);
            when(result.hasErrors()).thenReturn(true);

            WizytaDto wizytaDto = new WizytaDto();
            String redirect = wizytaController.createWizyta(wizytaDto, result);

        }

        @Test
        @DisplayName("Stworzenie wizyty dobrze")
        void shouldCreateWizytaWithKlientId() {
            BindingResult result = mock(BindingResult.class);
            Klient klient = new Klient();
            klientRepository.save(klient);

            WizytaDto wizytaDto = new WizytaDto();
            wizytaDto.setKlientId(klient.getId());
            String redirect = wizytaController.createWizyta(wizytaDto, result);

        }

        @Test
        @DisplayName("Edycja wizyty")
        void shoulEditWizyta() {
            WizytaDto wizytaDto = new WizytaDto();
            ExtendedModelMap model = new ExtendedModelMap();
            wizytaController.editWizyta(model,1);
        }

        @Test
        @DisplayName("Edycja wizyty nie null i klient nie null")
        void shoulEditWizytaNotNull() {
            Klient klient = new Klient();
            klientRepository.save(klient);

            Wizyta wizyta = new Wizyta();
            wizyta.setDataWizyty(Date.valueOf(LocalDate.now()));
            wizyta.setGodzina("10.00");
            wizyta.setPokoj("101");
            wizyta.setCzyOplacona(false);
            wizyta.setNaleznosc("100 zł");
            wizyta.setSposobPlatnosci("gotówka");
            wizyta.setKlient(klient);
            wizytaRepository.save(wizyta);

            ExtendedModelMap model = new ExtendedModelMap();
            wizytaController.editWizyta(model,wizyta.getId());
        }

        @Test
        @DisplayName("Edycja wizyty nie null i klient null")
        void shoulEditWizytaNotNullKlientNull() {
            Wizyta wizyta = new Wizyta();
            wizyta.setDataWizyty(Date.valueOf(LocalDate.now()));
            wizyta.setGodzina("10.00");
            wizyta.setPokoj("101");
            wizyta.setCzyOplacona(false);
            wizyta.setNaleznosc("100 zł");
            wizyta.setSposobPlatnosci("gotówka");
            wizytaRepository.save(wizyta);

            ExtendedModelMap model = new ExtendedModelMap();
            wizytaController.editWizyta(model,wizyta.getId());
        }

        @Test
        @DisplayName("Edycja wizyty2")
        void shoulEditWizyta2() {
            BindingResult result = mock(BindingResult.class);
            WizytaDto wizytaDto = new WizytaDto();
            ExtendedModelMap model = new ExtendedModelMap();
            wizytaController.editWizyta(model,1,wizytaDto,result);
        }

        @Test
        @DisplayName("Edycja wizyty2 not null")
        void shoulEditWizyta2NotNull() {
            Wizyta wizyta = new Wizyta();
            wizytaRepository.save(wizyta);
            Klient klient = new Klient();
            klientRepository.save(klient);

            ExtendedModelMap model = new ExtendedModelMap();
            BindingResult result = mock(BindingResult.class);

            WizytaDto wizytaDto = new WizytaDto();
            wizytaDto.setDataWizyty(Date.valueOf(LocalDate.now()));
            wizytaDto.setGodzina("10.00");
            wizytaDto.setPokoj("101");
            wizytaDto.setCzyOplacona(false);
            wizytaDto.setNaleznosc("100 zł");
            wizytaDto.setSposobPlatnosci("gotówka");
            wizytaDto.setKlientId(klient.getId());
            wizytaController.editWizyta(model,wizyta.getId(),wizytaDto,result);
        }
        @Test
        @DisplayName("Edycja wizyty2 not null has errors")
        void shoulEditWizyta2NotNullHasErrors() {
            Wizyta wizyta = new Wizyta();
            wizytaRepository.save(wizyta);


            ExtendedModelMap model = new ExtendedModelMap();
            BindingResult result = mock(BindingResult.class);
            when(result.hasErrors()).thenReturn(true);

            WizytaDto wizytaDto = new WizytaDto();
            wizytaDto.setDataWizyty(Date.valueOf(LocalDate.now()));
            wizytaDto.setGodzina("10.00");
            wizytaDto.setPokoj("101");
            wizytaDto.setCzyOplacona(false);
            wizytaDto.setNaleznosc("100 zł");
            wizytaDto.setSposobPlatnosci("gotówka");
            wizytaController.editWizyta(model,wizyta.getId(),wizytaDto,result);
        }

        @Test
        @DisplayName("Edycja wizyty2 not null")
        void shoulEditWizyta2NotNullKlientNull() {
            Wizyta wizyta = new Wizyta();
            wizytaRepository.save(wizyta);

            ExtendedModelMap model = new ExtendedModelMap();
            BindingResult result = mock(BindingResult.class);

            WizytaDto wizytaDto = new WizytaDto();
            wizytaDto.setDataWizyty(Date.valueOf(LocalDate.now()));
            wizytaDto.setGodzina("10.00");
            wizytaDto.setPokoj("101");
            wizytaDto.setCzyOplacona(false);
            wizytaDto.setNaleznosc("100 zł");
            wizytaDto.setSposobPlatnosci("gotówka");
            wizytaController.editWizyta(model,wizyta.getId(),wizytaDto,result);
        }
        @Test
        @DisplayName("Usuń wizyte")
        void shoulDeleteWizyta() {

            wizytaController.deleteWizyta(1);
        }

        @Test
        @DisplayName("Usuwa wizytę z repozytorium")
        void shouldDeleteWizyta2() {
            // given
            Klient klient = new Klient();
            klientRepository.save(klient);

            Wizyta wizyta = new Wizyta();
            wizyta.setDataWizyty(Date.valueOf(LocalDate.now()));
            wizyta.setGodzina("10:00"); // jeśli to String
            wizyta.setPokoj("101");
            wizyta.setCzyOplacona(false);
            wizyta.setNaleznosc("100.00"); // jeśli to String
            wizyta.setSposobPlatnosci("gotówka");
            wizyta.setUwagi("Testowa wizyta");
            wizyta.setKlient(klient);

            wizytaRepository.save(wizyta);
            Integer id = wizyta.getId();

            // when
            wizytaController.deleteWizyta(id);

            // then
            assertFalse(wizytaRepository.findById(id).isPresent());
        }
    }
}
