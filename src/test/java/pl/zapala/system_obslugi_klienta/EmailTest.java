package pl.zapala.system_obslugi_klienta;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import pl.zapala.system_obslugi_klienta.validators.EmailValidator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class EmailTest {
    private EmailValidator validator;
    @BeforeEach
    void setUp() {
        validator = new EmailValidator();
    }

    @Nested
    @DisplayName("Valid Email")
    class ValidTextTests {

        @ParameterizedTest
        @ValueSource(strings = {
                "kacper@gmail.com", // Poprawny PESEL dla osoby urodzonej 14-05-1944
                "jakub@o2.pl", // Poprawny PESEL dla osoby urodzonej 08-07-2002
                "maciek@for.com", // Poprawny PESEL dla osoby urodzonej 05-09-1990
                "Juan-Martin@xd.pl", // Poprawny PESEL dla osoby urodzonej 17-12-1980
                "qweqwcq@asd.fr"  // Poprawny PESEL dla osoby urodzonej 02-01-1967
        })
        @DisplayName("Poprawne maile powinny przechodzić walidację")
        void validTextShouldPassValidation(String text) {
            assertTrue(validator.isValid(text, null),
                    "Prawidłowy mail powinien przejść walidację: " + text);
        }
    }

    @Nested
    @DisplayName("Invalid Email")
    class InValidTextTests {

        @ParameterizedTest
        @ValueSource(strings = {
                "@gmail.com", // Poprawny PESEL dla osoby urodzonej 14-05-1944
                "kuba@gmail.", // Poprawny PESEL dla osoby urodzonej 08-07-2002
                "kuba@.com", // Poprawny PESEL dla osoby urodzonej 05-09-1990
                "a@g.c", // Poprawny PESEL dla osoby urodzonej 17-12-1980
                "qweqwcq.com"  // Poprawny PESEL dla osoby urodzonej 02-01-1967
        })
        @DisplayName("Niepoprawne maile nie powinny przechodzić walidacji")
        void validTextShouldFailValidation(String text) {
            assertFalse(validator.isValid(text, null),
                    "Prawidłowy mail powinien przejść walidację: " + text);
        }
    }


}