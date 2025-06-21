package pl.zapala.system_obslugi_klienta;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import pl.zapala.system_obslugi_klienta.models.Klient;
import pl.zapala.system_obslugi_klienta.models.KlientDto;
import pl.zapala.system_obslugi_klienta.controllers.KlientController;
import pl.zapala.system_obslugi_klienta.models.Pracownik;
import pl.zapala.system_obslugi_klienta.repositories.KlientRepository;
import pl.zapala.system_obslugi_klienta.repositories.PracownikRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
public class KlientControllerTest {

    @Autowired
    private KlientController klientController;
    @Autowired
    private KlientRepository klientRepository;
    private KlientDto klientDto;
    @Mock
    private KlientRepository klientRepository1;

    @InjectMocks
    private KlientController klientController1;

    @BeforeEach
    void setUp() {
        klientRepository.deleteAll();
        klientDto = new KlientDto();
        klientDto.setImie("Anna");
        klientDto.setNazwisko("Kowalska");
        klientDto.setDataUrodzenia(Date.valueOf(LocalDate.of(1995, 5, 10)));
        klientDto.setEmail("anna@example.com");
        klientDto.setKodPocztowy("00-001");
        klientDto.setUlicaNumerDomu("ul. Kwiatowa 12");
        klientDto.setMiejscowosc("Warszawa");
        klientDto.setNumerTelefonu("+48 123 456 789");
    }

    @Nested
    @DisplayName("Testy KlientController")
    class KlientValidation {

        @DisplayName("Authentykacja - loggedPracownik")
        @Test
        void loggedPracownikTest() {

            Authentication authentication = mock(Authentication.class);
            when(authentication.isAuthenticated()).thenReturn(true);
            when(authentication.getName()).thenReturn("jan@example.com");

            SecurityContext securityContext = mock(SecurityContext.class);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            SecurityContextHolder.setContext(securityContext);

            PracownikRepository pracownikRepo = mock(PracownikRepository.class);
            Pracownik expectedPracownik = new Pracownik();
            expectedPracownik.setEmail("jan@example.com");
            expectedPracownik.setId(1);
            expectedPracownik.getEmail();
            expectedPracownik.getHaslo();
            expectedPracownik.setHaslo("asd123!@#");
            expectedPracownik.getTotpSecret();
            expectedPracownik.setTotpSecret("123");
            when(pracownikRepo.findByEmail("jan@example.com")).thenReturn(expectedPracownik);

            Model model = mock(Model.class);

            KlientController controller = new KlientController(
                    mock(KlientRepository.class),
                    pracownikRepo);

            controller.loggedPracownik(model);
            verify(model).addAttribute("pracownik", expectedPracownik);
        }

        @Test
        @DisplayName("Pobranie klientow")
        void shouldGetKlienci() {
            Klient klient = new Klient();
            klientRepository.save(klient);
            ExtendedModelMap model = new ExtendedModelMap();

            String viewName = klientController.getKlienci(model);
            assertEquals("klienci/index", viewName);
            List<Klient> Klienci = klientRepository.findAll();
            assertEquals(klient.getId(),Klienci.get(0).getId());
        }

        @Test
        @DisplayName("Stworzenie klienta(model)")
        void CreateKlient() {
            ExtendedModelMap model = new ExtendedModelMap();
            String viewName = klientController.createKlient(model);
            assertEquals("klienci/dodaj", viewName);
        }
        @Test
        @DisplayName("Stworzenie klienta - poprawnie")
        void shouldCreateClientSuccessfully() {
            BindingResult result = new BeanPropertyBindingResult(klientDto, "klientDto1");
            String viewName = klientController.createKlient(klientDto, result);

            assertEquals("redirect:/klienci", viewName);
            assertEquals("Anna", klientRepository.findAll().get(0).getImie());
        }

        @Test
        @DisplayName("Stworzenie klienta - ten sam mail błąd")
        void CreateKlientSameMail() {
            Klient klient = new Klient();
            klient.setEmail("anna@example.com");
            klientRepository.save(klient);
            klientDto.setEmail(klient.getEmail());

            BindingResult result = new BeanPropertyBindingResult(klientDto, "klientDto");
            String viewName = klientController.createKlient(klientDto, result);
            assertTrue(result.hasErrors());
            assertEquals(result.getFieldError("email").getDefaultMessage(),
                    "Klient o takim adresie e-mail już istnieje.");
            assertEquals("klienci/dodaj", viewName);
        }

        @Test
        @DisplayName("Edytowanie klienta(model) - poprawnie")
        void EditKlient1() {
            Klient klient = new Klient();
            klient.setImie("Jan");
            klient.setNazwisko("Kowalski");
            klient.setEmail("jan@example.com");
            klient.setDataUrodzenia(Date.valueOf("1990-01-01"));
            klient.setUlicaNumerDomu("Testowa 1");
            klient.setKodPocztowy("00-001");
            klient.setMiejscowosc("Warszawa");
            klient.setNumerTelefonu("123456789");
            klientRepository.save(klient);
            ExtendedModelMap model = new ExtendedModelMap();
            String viewName = klientController.editKlient(model, klient.getId());
            assertEquals("klienci/edytuj", viewName);
        }

        @Test
        @DisplayName("Edytowanie klienta(model) - brak klienta")
        void EditKlient1Null() {
            ExtendedModelMap model = new ExtendedModelMap();
            klientController.editKlient(model,1);

            String viewName = klientController.editKlient(model,1);
            assertEquals("redirect:/klienci", viewName);
        }

        @Test
        @DisplayName("Edytowanie klienta - poprawnie")
        void EditKlient2Success() {
            Klient klient = new Klient();
            klient.setImie("Marek");
            klientRepository.save(klient);

            ExtendedModelMap model = new ExtendedModelMap();
            BindingResult result = new BeanPropertyBindingResult(klientDto, "klientDto");
            String viewName = klientController.editKlient(model,klient.getId(),klientDto,result);
            assertEquals("redirect:/klienci", viewName);

            List<Klient> Klienci = klientRepository.findAll();
            assertNotEquals("Marek",Klienci.get(0).getImie());
            assertEquals("Anna",Klienci.get(0).getImie());
        }


        @Test
        @DisplayName("Edytowanie klienta - brak klienta")
        void EditKlient2Null() {
            ExtendedModelMap model = new ExtendedModelMap();
            BindingResult result = mock(BindingResult.class);
            String viewName = klientController.editKlient(model,123,klientDto,result);
            assertEquals("redirect:/klienci", viewName);
        }

        @Test
        @DisplayName("Edytowanie klienta - result błąd")
        void EditKlient2HasErrors() {
            Klient klient = new Klient();
            klientRepository.save(klient);

            ExtendedModelMap model = new ExtendedModelMap();
            BindingResult result = mock(BindingResult.class);
            when(result.hasErrors()).thenReturn(true);
            klientController.editKlient(model,klient.getId(),klientDto,result);
            String viewName = klientController.editKlient(model,klient.getId(),klientDto,result);
            assertEquals("klienci/edytuj", viewName);
        }


        @Test
        @DisplayName("Edytowanie klienta - ten sam mail błąd")
        void EditKlient2SameMail() {
            int klientId = 1;
            Klient klient = new Klient();
            klient.setId(klientId);
            klient.setEmail("anna@example.com");

            when(klientRepository1.findById(klientId)).thenReturn(Optional.of(klient));
            doThrow(new RuntimeException("Duplicate email")).when(klientRepository1).save(any(Klient.class));

            BindingResult result = new BeanPropertyBindingResult(klientDto, "klientDto");
            Model model = new ExtendedModelMap();
            String viewName = klientController1.editKlient(model, klientId, klientDto, result);

            assertTrue(result.hasErrors());
            assertEquals("Klient o takim adresie e-mail już istnieje.", result.getFieldError("email").getDefaultMessage());
            assertEquals("klienci/edytuj", viewName);
        }



        @Test
        @DisplayName("Usuwanie klienta - poprawnie")
        void DeleteKlientSuccess() {
            Klient klient = new Klient();
            klient.setImie("Jan");
            klient.setNazwisko("Nowak");
            klient.setEmail("jan@example.com");
            klient.setDataUrodzenia(Date.valueOf("1990-01-01"));
            klient.setUlicaNumerDomu("Testowa 1");
            klient.setKodPocztowy("00-001");
            klient.setMiejscowosc("Warszawa");
            klient.setNumerTelefonu("123456789");
            klient = klientRepository.save(klient);

            String viewName = klientController.deleteKlient(klient.getId());

            assertEquals("redirect:/klienci", viewName);
            assertFalse(klientRepository.findById(klient.getId()).isPresent());
        }

        @Test
        @DisplayName("Usuwanie klienta - brak klienta")
        void DeleteKlientNull() {
            String viewName = klientController.deleteKlient(1);
            assertEquals("redirect:/klienci", viewName);
        }


    }
}
