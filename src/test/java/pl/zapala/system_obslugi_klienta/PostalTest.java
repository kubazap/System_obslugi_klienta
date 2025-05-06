package pl.zapala.system_obslugi_klienta;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import pl.zapala.system_obslugi_klienta.validators.PostalValidator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
class PostalTest {
    private PostalValidator validator;
    @BeforeEach
    void setUp() {
        validator = new PostalValidator();
    }

    @Nested
    @DisplayName("Valid Postal Code")
    class ValidTextTests {

        @ParameterizedTest
        @ValueSource(strings = {
                "12-123",
                "92-532",
                "10-100",
        })
        @DisplayName("Poprawne kody powinny przechodzić walidację")
        void validTextShouldPassValidation(String text) {
            assertTrue(validator.isValid(text, null),
                    "Prawidłowy kod powinien przejść walidację: " + text);
        }
    }

    @Nested
    @DisplayName("Invalid Postal Code")
    class InValidTextTests {

        @ParameterizedTest
        @ValueSource(strings = {
                "12 123", // Poprawny PESEL dla osoby urodzonej 14-05-1944
                "2-132", // Poprawny PESEL dla osoby urodzonej 08-07-2002
                "12-12", // Poprawny PESEL dla osoby urodzonej 05-09-1990
                "12-141a", // Poprawny PESEL dla osoby urodzonej 17-12-1980
                "23-a21"  // Poprawny PESEL dla osoby urodzonej 02-01-1967
        })
        @DisplayName("Niepoprawne kody nie powinny przechodzić walidacji")
        void validTextShouldFailValidation(String text) {
            assertFalse(validator.isValid(text, null),
                    "Prawidłowy kod powinien przejść walidację: " + text);
        }
    }


}