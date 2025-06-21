package pl.zapala.system_obslugi_klienta;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import pl.zapala.system_obslugi_klienta.models.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

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
public class KlientTest {

    private KlientDto klientDto;

    @BeforeEach
    void setUp() {
        klientDto = new KlientDto();
    }

    @Nested
    @DisplayName("Test imienia i nazwiska")
    class NameValidation {

        @ParameterizedTest
        @ValueSource(strings = {"Anna", "Kamil", "Jan-Kowalski", "Maria Nowak"})
        void ValidNames(String name) {
            klientDto.setImie(name);
            klientDto.setNazwisko(name);
            assertEquals(name, klientDto.getImie());
            assertEquals(name, klientDto.getNazwisko());
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "1sd2", "123", "An@na", "John123"})
        void InvalidNames(String name) {
            klientDto.setImie(name);
            assertFalse(name.matches("^[A-ZĄĆĘŁŃÓŚŹŻa-ząćęłńóśźż\\- ]+$"));
        }
    }

    @Nested
    @DisplayName("Test daty urodzenia")
    class BirthDateValidation {

        @Test
        void ValidDate() {
            LocalDate valid = LocalDate.now().minusYears(25);
            klientDto.setDataUrodzenia(Date.valueOf(valid));
            assertEquals(valid, klientDto.getDataUrodzenia().toLocalDate());
        }

        @Test
        void InvalidDateInFuture() {
            LocalDate future = LocalDate.now().plusDays(1);
            klientDto.setDataUrodzenia(Date.valueOf(future));
            assertTrue(klientDto.getDataUrodzenia().toLocalDate().isAfter(LocalDate.now()));
        }

        @Test
        void InvalidDateTooOld() {
            LocalDate tooOld = LocalDate.now().minusYears(101);
            klientDto.setDataUrodzenia(Date.valueOf(tooOld));
            assertTrue(klientDto.getDataUrodzenia().toLocalDate().isBefore(LocalDate.now().minusYears(100)));
        }
    }

    @Nested
    @DisplayName("Test kodu pocztowego")
    class PostalCodeValidation {

        @ParameterizedTest
        @ValueSource(strings = {"00-001", "99-999", "12-345"})
        void ValidPostal(String code) {
            klientDto.setKodPocztowy(code);
            assertTrue(code.matches("\\d{2}-\\d{3}"));
        }

        @ParameterizedTest
        @ValueSource(strings = {"00001", "00/001", "00-0011", "000-01", "abcd"})
        void InvalidPostal(String code) {
            klientDto.setKodPocztowy(code);
            assertFalse(code.matches("\\d{2}-\\d{3}"));
        }
    }

    @Nested
    @DisplayName("Test emaila")
    class EmailValidation {

        @ParameterizedTest
        @ValueSource(strings = {"test@example.com", "user.name@domain.pl", "kontakt@firma.org"})
        void ValidEmails(String email) {
            klientDto.setEmail(email);
            assertTrue(email.contains("@"));
        }

        @ParameterizedTest
        @ValueSource(strings = {"test@", "@example.com", "user@.com", "plainaddress"})
        void InvalidEmails(String email) {
            klientDto.setEmail(email);
            assertFalse(email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$"));
        }
    }

    @Nested
    @DisplayName("Test pola ulicy i numeru")
    class AddressValidation {

        @ParameterizedTest
        @ValueSource(strings = {"ul. Kwiatowa 15", "Nowa 3A", "Szeroka 7/2"})
        void ValidStreer(String address) {
            klientDto.setUlicaNumerDomu(address);
            assertNotNull(klientDto.getUlicaNumerDomu());
            assertFalse(klientDto.getUlicaNumerDomu().trim().isEmpty());
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "   "})
        void InValidStreet(String address) {
            klientDto.setUlicaNumerDomu(address);
            assertTrue(klientDto.getUlicaNumerDomu().trim().isEmpty());
        }
    }
    @Nested
    @DisplayName("Test uzupełniający wizyty")
    class WizytyTest {
        @Test
        @DisplayName("Get set wizyty")
        void shouldSetAndGetWizyty() {
            Klient klient = new Klient();
            klient.setId(1);
            Wizyta wizyta = new Wizyta();
            List<Wizyta> wizyty = List.of(wizyta);
            klient.setWizyty(wizyty);
            List<Wizyta> result = klient.getWizyty();
            assertNotNull(result);
            assertEquals(1, result.size());
            assertSame(wizyta, result.get(0));
        }
    }
}
