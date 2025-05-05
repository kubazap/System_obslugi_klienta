package pl.zapala.system_obslugi_klienta;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import pl.zapala.system_obslugi_klienta.validators.NameValidator;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class NameTest {
    private NameValidator validator;
    @BeforeEach
    void setUp() {
        validator = new NameValidator();
    }

    @Nested
    @DisplayName("Valid TEXT")
    class ValidTextTests {

        @ParameterizedTest
        @ValueSource(strings = {
                "Kacper", // Poprawny PESEL dla osoby urodzonej 14-05-1944
                "Jakub", // Poprawny PESEL dla osoby urodzonej 08-07-2002
                "Maciek", // Poprawny PESEL dla osoby urodzonej 05-09-1990
                "Juan-Martin", // Poprawny PESEL dla osoby urodzonej 17-12-1980
                "qweqwcq"  // Poprawny PESEL dla osoby urodzonej 02-01-1967
        })
        @DisplayName("Poprawne teksty powinny przechodzić walidację")
        void validTextShouldPassValidation(String text) {
            assertTrue(validator.isValid(text, null),
                    "Prawidłowy PESEL powinien przejść walidację: " + text);
        }
    }

    @Nested
    @DisplayName("Invalid TEXT")
    class InValidTextTests {

        @ParameterizedTest
        @ValueSource(strings = {
                "123", // Poprawny PESEL dla osoby urodzonej 14-05-1944
                "Jakub1", // Poprawny PESEL dla osoby urodzonej 08-07-2002
                "...", // Poprawny PESEL dla osoby urodzonej 05-09-1990
                "????", // Poprawny PESEL dla osoby urodzonej 17-12-1980
                "qweqwcq.com"  // Poprawny PESEL dla osoby urodzonej 02-01-1967
        })
        @DisplayName("Niepoprawne teksty nie powinny przechodzić walidacji")
        void validTextShouldFailValidation(String text) {
            assertFalse(validator.isValid(text, null),
                    "Prawidłowy PESEL powinien przejść walidację: " + text);
        }
    }


}