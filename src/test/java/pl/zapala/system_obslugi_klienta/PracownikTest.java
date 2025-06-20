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
public class PracownikTest {

    private Pracownik pracownik;
    private PracownikDto pracownikDto;


    @BeforeEach
    void setUp() {

        pracownik = new Pracownik();
        pracownikDto = new PracownikDto();
    }

    @Nested
    @DisplayName("Test imienia i nazwiska")
    class PracownikValidation {
        @Test
        @DisplayName("Edycja dokumentu")
        void PracownikAddonTests() {
            pracownik.setId(1);
            pracownik.getEmail();
            pracownik.getHaslo();
            pracownik.setHaslo("asd123!@#");
            pracownik.getTotpSecret();
            pracownik.setTotpSecret("123");
            pracownikDto.getImie();
            pracownikDto.getNazwisko();
        }

    }
}
