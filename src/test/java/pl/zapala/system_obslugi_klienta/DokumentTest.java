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
import pl.zapala.system_obslugi_klienta.models.DokumentDto;

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
public class DokumentTest {

    private DokumentDto dokument;

    @BeforeEach
    void setUp() {
        dokument = new DokumentDto();
    }

    @Nested
    @DisplayName("Test nazwy dokumentu")
    class NameValidation {

        @ParameterizedTest
        @ValueSource(strings = {"Stypendium socjalne", "umowa o prace", "cośtam", "umowa kupna-sprzedaży"})
        void shouldAcceptValidNames(String name) {
            dokument.setNazwaDokumentu(name);
            assertEquals(name, dokument.getNazwaDokumentu());
        }
    }

    @Nested
    @DisplayName("Test daty dodania")
    class BirthDateValidation {

        @Test
        void shouldAcceptValidDate() {
            LocalDate valid = LocalDate.now();
            dokument.setDataDodania(Date.valueOf(valid));
            assertEquals(valid, dokument.getDataDodania().toLocalDate());
        }

        @Test
        void shouldRejectDateInFuture() {
            LocalDate future = LocalDate.now().plusDays(1);
            dokument.setDataDodania(Date.valueOf(future));
            assertTrue(dokument.getDataDodania().toLocalDate().isAfter(LocalDate.now()));
        }

        @Test
        void shouldRejectDateTooOld() {
            LocalDate tooOld = LocalDate.now().minusYears(101);
            dokument.setDataDodania(Date.valueOf(tooOld));
            assertTrue(dokument.getDataDodania().toLocalDate().isBefore(LocalDate.now().minusYears(100)));
        }
    }

    @Nested
    @DisplayName("Test typu")
    class PostalCodeValidation {

        @ParameterizedTest
        @ValueSource(strings = {"Umowa", "Faktura", "Formularz"})
        void shouldAcceptType(String name) {
            dokument.setTyp(name);
            assertEquals(name, dokument.getTyp());
        }
    }

    @Nested
    @DisplayName("Test statusu")
    class StatusValidation {

        @ParameterizedTest
        @ValueSource(strings = {"true", "false"})
        void shouldAcceptValidEmails(Boolean status) {
            dokument.setStatus(status);
            assertEquals(status, dokument.getStatus());
        }

    }

    @Nested
    @DisplayName("Test uwag")
    class UwagaValidation {

        @ParameterizedTest
        @ValueSource(strings = {"asdhkhk12hdkhkask sakdj hkh1kj ", "asd12dj1 dh12dh21", "asd 12k d1k2hd kj2"})
        void shouldAcceptNonEmpty(String text) {
            dokument.setUwagi(text);
            assertNotNull(dokument.getUwagi());
            assertEquals(text, dokument.getUwagi());
        }
    }
}
