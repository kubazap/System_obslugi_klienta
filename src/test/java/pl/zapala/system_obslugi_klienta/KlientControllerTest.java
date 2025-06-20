package pl.zapala.system_obslugi_klienta;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
import pl.zapala.system_obslugi_klienta.controllers.WizytaController;
import pl.zapala.system_obslugi_klienta.models.Klient;
import pl.zapala.system_obslugi_klienta.models.KlientDto;
import pl.zapala.system_obslugi_klienta.controllers.KlientController;
import pl.zapala.system_obslugi_klienta.models.Pracownik;
import pl.zapala.system_obslugi_klienta.models.Wizyta;
import pl.zapala.system_obslugi_klienta.repositories.KlientRepository;
import pl.zapala.system_obslugi_klienta.repositories.PracownikRepository;
import pl.zapala.system_obslugi_klienta.repositories.WizytaRepository;

import java.sql.Date;
import java.time.LocalDate;

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
    }

    @Nested
    @DisplayName("Poprawne dodanie klienta")
    class KlientValidation {

        @DisplayName("Authentykacja - loggedPracownik")
        @Test
        void loggedPracownik_shouldAddLoggedUserToModel_whenAuthenticated() {

            Authentication authentication = mock(Authentication.class);
            when(authentication.isAuthenticated()).thenReturn(true);
            when(authentication.getName()).thenReturn("jan@example.com");


            SecurityContext securityContext = mock(SecurityContext.class);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            SecurityContextHolder.setContext(securityContext);


            PracownikRepository pracownikRepo = mock(PracownikRepository.class);
            Pracownik expectedPracownik = new Pracownik();
            expectedPracownik.setEmail("jan@example.com");
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
        void shouldGetWizyty() {
            ExtendedModelMap model = new ExtendedModelMap();
            klientController.getKlienci(model);
        }

        @Test
        @DisplayName("Stworzenie klienta")
        void CreateKlient() {
            ExtendedModelMap model = new ExtendedModelMap();
            klientController.createKlient(model);
        }
        @Test
        @DisplayName("Powinien dodać klienta poprawnie")
        void shouldAddClientSuccessfully() {

            KlientDto klientDto1 = new KlientDto();
            klientDto1.setImie("Anna");
            klientDto1.setNazwisko("Kowalska");
            klientDto1.setEmail("anna@example.com");
            klientDto1.setDataUrodzenia(Date.valueOf("1995-05-10"));
            klientDto1.setKodPocztowy("00-001");
            klientDto1.setUlicaNumerDomu("ul. Kwiatowa 12");
            klientDto1.setMiejscowosc("Warszawa");
            klientDto1.setNumerTelefonu("123456789");

            BindingResult result = new BeanPropertyBindingResult(klientDto1, "klientDto1");

            String viewName = klientController.createKlient(klientDto1, result);

            assertEquals("redirect:/klienci", viewName);
            assertEquals("Anna", klientRepository.findAll().get(0).getImie());
        }

        @Test
        @DisplayName("CreateKlientSameMail")
        void CreateKlientSameMail() {
            Klient klient = new Klient();
            klient.setEmail("kacper.wojtyra1@gmail.com");
            klientRepository.save(klient);

            KlientDto klientDto1 = new KlientDto();
            klientDto1.setImie("Anna");
            klientDto1.setNazwisko("Kowalska");
            klientDto1.setEmail(klient.getEmail());
            klientDto1.setDataUrodzenia(Date.valueOf("1995-05-10"));
            klientDto1.setKodPocztowy("00-001");
            klientDto1.setUlicaNumerDomu("ul. Kwiatowa 12");
            klientDto1.setMiejscowosc("Warszawa");
            klientDto1.setNumerTelefonu("123456789");

            BindingResult result = new BeanPropertyBindingResult(klientDto1, "klientDto1");

            klientController.createKlient(klientDto1, result);
        }

        @Test
        @DisplayName("CreateKlientHasErrors")
        void CreateKlientHasErrors() {
            KlientDto klientDto1 = new KlientDto();
            klientDto1.setImie("Anna");
            klientDto1.setNazwisko("Kowalska");
            klientDto1.setEmail("anna12@example.com");
            klientDto1.setDataUrodzenia(Date.valueOf("1995-05-10"));
            klientDto1.setKodPocztowy("00-001");
            klientDto1.setUlicaNumerDomu("ul. Kwiatowa 12");
            klientDto1.setMiejscowosc("Warszawa");
            klientDto1.setNumerTelefonu("123456789");

            BindingResult result = mock(BindingResult.class);
            when(result.hasErrors()).thenReturn(true);

            klientController.createKlient(klientDto1, result);
        }


        @Test
        @DisplayName("Powinien edytowac klienta null")
        void EditKlient1Null() {
            ExtendedModelMap model = new ExtendedModelMap();
            klientController.editKlient(model,1);
        }
        @Test
        @DisplayName("Powinien edytowac klienta poprawnie")
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
            klientController.editKlient(model, klient.getId());
        }

        @Test
        @DisplayName("Powinien edytowac klienta2 null")
        void EditKlient2Null() {
            ExtendedModelMap model = new ExtendedModelMap();
            BindingResult result = mock(BindingResult.class);
            klientController.editKlient(model,123,klientDto,result);
        }
        /*
                @Test
                @DisplayName("Powinien edytowac klienta2 null")
                void EditKlient2Ex() {
                    ExtendedModelMap model = new ExtendedModelMap();
                    BindingResult result = new BeanPropertyBindingResult(klientDto, "klientDto");
                    when(result.hasErrors()).thenReturn(true);
                    klientController.editKlient(model,123,klientDto,result);
                }
         */
        @Test
        @DisplayName("Powinien edytowac klienta2 errpp")
        void EditKlient2HasErrors() {
            Klient klient = new Klient();
            klientRepository.save(klient);

            ExtendedModelMap model = new ExtendedModelMap();
            BindingResult result = mock(BindingResult.class);
            when(result.hasErrors()).thenReturn(true);
            klientController.editKlient(model,klient.getId(),klientDto,result);
        }
        @Test
        @DisplayName("Powinien edytowac klienta2 poprawnie")
        void EditKlient2Success() {
            Klient klient = new Klient();
            klientRepository.save(klient);

            KlientDto klientDto = new KlientDto();
            klientDto.setImie("Anna");
            klientDto.setNazwisko("Kowalska");
            klientDto.setEmail("anna12@example.com");
            klientDto.setDataUrodzenia(Date.valueOf("1995-05-10"));
            klientDto.setKodPocztowy("00-001");
            klientDto.setUlicaNumerDomu("ul. Kwiatowa 12");
            klientDto.setMiejscowosc("Warszawa");
            klientDto.setNumerTelefonu("123456789");

            ExtendedModelMap model = new ExtendedModelMap();
            BindingResult result = new BeanPropertyBindingResult(klientDto, "klientDto");
            klientController.editKlient(model,klient.getId(),klientDto,result);
        }

        @Test
        @DisplayName("Powinien edytowac klienta2 poprawnie")
        void EditKlient2SuccessSameMail() {
            Klient klient = new Klient();
            klient.setEmail("kacper.wojtyra1@gmail.com");
            klientRepository.save(klient);
            Klient klient1 = new Klient();
            klient1.setEmail("kacper.wojtyra1@gmail.com");
            klientRepository.save(klient1);

            KlientDto klientDto = new KlientDto();
            klientDto.setImie("Anna");
            klientDto.setNazwisko("Kowalska");
            klientDto.setEmail(klient.getEmail());
            klientDto.setDataUrodzenia(Date.valueOf("1995-05-10"));
            klientDto.setKodPocztowy("00-001");
            klientDto.setUlicaNumerDomu("ul. Kwiatowa 12");
            klientDto.setMiejscowosc("Warszawa");
            klientDto.setNumerTelefonu("123456789");

            ExtendedModelMap model = new ExtendedModelMap();
            BindingResult result = new BeanPropertyBindingResult(klientDto, "klientDto");
            klientController.editKlient(model,klient.getId(),klientDto,result);
        }
        @Test
        @DisplayName("Powinien usunąć klienta")
        void shouldDeleteClientSuccessfully() {
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


    }
}
