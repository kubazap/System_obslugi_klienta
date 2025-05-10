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
import org.springframework.test.context.TestPropertySource;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import pl.zapala.system_obslugi_klienta.models.Klient;
import pl.zapala.system_obslugi_klienta.models.KlientDto;
import pl.zapala.system_obslugi_klienta.controllers.KlientController;
import pl.zapala.system_obslugi_klienta.repositories.KlientRepository;

import java.sql.Date;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

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

        @Test
        @DisplayName("Powinien dodać klienta poprawnie")
        void shouldAddClientSuccessfully() {
            klientRepository.deleteAll(); // wyczyść bazę
            klientDto = new KlientDto();

            KlientDto klientDto1 = new KlientDto();
            klientDto1.setImie("Anna");
            klientDto1.setNazwisko("Kowalska");
            klientDto1.setEmail("anna@example.com");
            klientDto1.setDataUrodzenia(Date.valueOf("1995-05-10"));
            klientDto1.setKodPocztowy("00-001");
            klientDto1.setUlicaNumerDomu("ul. Kwiatowa 12");
            klientDto1.setMiejscowosc("Warszawa");
            klientDto1.setNumerTelefonu("123456789");
            // utwórz BindingResult ręcznie
            BindingResult result = new BeanPropertyBindingResult(klientDto1, "klientDto1");

            // wywołanie kontrolera
            String viewName = klientController.createKlient(klientDto1, result);

            // asercje
            assertEquals("redirect:/klienci", viewName);
            assertEquals("Anna", klientRepository.findAll().get(0).getImie());
        }

        @Test
        @DisplayName("Powinien edytowac klienta poprawnie")
        void shouldEditClientSuccessfully() {
            // Dodanie istniejącego klienta do bazy
            Klient klient = new Klient();
            klient.setImie("Jan");
            klient.setNazwisko("Kowalski");
            klient.setEmail("jan@example.com");
            klient.setDataUrodzenia(Date.valueOf("1990-01-01"));
            klient.setUlicaNumerDomu("Testowa 1");
            klient.setKodPocztowy("00-001");
            klient.setMiejscowosc("Warszawa");
            klient.setNumerTelefonu("123456789");
            klient = klientRepository.save(klient);

            // Nowe dane do edycji
            KlientDto klientDto = new KlientDto();
            klientDto.setImie("Janusz");
            klientDto.setNazwisko("Nowak");
            klientDto.setEmail("jan@example.com"); // ten sam email
            klientDto.setDataUrodzenia(Date.valueOf("1990-01-01"));
            klientDto.setUlicaNumerDomu("Zmodyfikowana 2");
            klientDto.setKodPocztowy("00-002");
            klientDto.setMiejscowosc("Kraków");
            klientDto.setNumerTelefonu("987654321");

            BindingResult result = new BeanPropertyBindingResult(klientDto, "klientDto");

            String viewName = klientController.editKlient(new ExtendedModelMap(), klient.getId(), klientDto, result);

            assertEquals("redirect:/klienci", viewName);

            Klient updated = klientRepository.findById(klient.getId()).orElseThrow();
            assertEquals("Janusz", updated.getImie());
            assertEquals("Nowak", updated.getNazwisko());
            assertEquals("Kraków", updated.getMiejscowosc());
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
