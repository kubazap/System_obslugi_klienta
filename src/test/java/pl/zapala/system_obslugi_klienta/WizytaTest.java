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
import pl.zapala.system_obslugi_klienta.models.WizytaDto;

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
public class WizytaTest {

    private WizytaDto wizyta;

    @BeforeEach
    void setUp() {
        wizyta = new WizytaDto();
    }

    @Nested
    @DisplayName("Test pokoj")
    class NameValidation {

        @ParameterizedTest
        @ValueSource(strings = {"Stypendium socjalne", "umowa o prace", "cośtam", "umowa kupna-sprzedaży"})
        void shouldAcceptValidNames(String name) {
            wizyta.setPokoj(name);
            assertEquals(name, wizyta.getPokoj());
        }
    }

    @Nested
    @DisplayName("Test daty wizyty")
    class DateValidation {

        @Test
        void shouldAcceptValidDate() {
            LocalDate valid = LocalDate.now().plusDays(1);
            wizyta.setDataWizyty(Date.valueOf(valid));
            assertEquals(valid, wizyta.getDataWizyty().toLocalDate());
        }

        @Test
        void shouldRejectDateInFuture() {
            LocalDate past = LocalDate.now().minusDays(1);
            wizyta.setDataWizyty(Date.valueOf(past));
            assertTrue(wizyta.getDataWizyty().toLocalDate().isBefore(LocalDate.now()));
        }

        @Test
        void shouldRejectDateTooOld() {
            LocalDate tooOld = LocalDate.now().minusYears(101);
            wizyta.setDataWizyty(Date.valueOf(tooOld));
            assertTrue(wizyta.getDataWizyty().toLocalDate().isBefore(LocalDate.now().minusYears(100)));
        }
    }

    @Nested
    @DisplayName("Test godziny")
    class PostalCodeValidation {

        @ParameterizedTest
        @ValueSource(strings = {"12.00", "13.00", "14.00"})
        void shouldAcceptType(String name) {
            wizyta.setGodzina(name);
            assertEquals(name, wizyta.getGodzina());
        }
    }


}
