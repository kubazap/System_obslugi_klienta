package pl.zapala.system_obslugi_klienta;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import pl.zapala.system_obslugi_klienta.validators.TimeValidator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class TimeTest {
    private TimeValidator validator;
    @BeforeEach
    void setUp() {
        validator = new TimeValidator();
    }

    @Nested
    @DisplayName("Valid Time")
    class ValidTextTests {

        @ParameterizedTest
        @ValueSource(strings = {
                "12.12", // Poprawny PESEL dla osoby urodzonej 14-05-1944
                "09.12", // Poprawny PESEL dla osoby urodzonej 08-07-2002
                "00.00", // Poprawny PESEL dla osoby urodzonej 05-09-1990
                "23.59", // Poprawny PESEL dla osoby urodzonej 17-12-1980
                "10.10"  // Poprawny PESEL dla osoby urodzonej 02-01-1967
        })
        @DisplayName("Poprawne godziny powinny przechodzić walidację")
        void validTextShouldPassValidation(String text) {
            assertTrue(validator.isValid(text, null),
                    "Prawidłowa godzina powinna przejść walidację: " + text);
        }
    }

    @Nested
    @DisplayName("Invalid Time")
    class InValidTextTests {

        @ParameterizedTest
        @ValueSource(strings = {
                "0.00", // Poprawny PESEL dla osoby urodzonej 14-05-1944
                "12.60", // Poprawny PESEL dla osoby urodzonej 08-07-2002
                "24.00", // Poprawny PESEL dla osoby urodzonej 05-09-1990
                "111.11", // Poprawny PESEL dla osoby urodzonej 17-12-1980
                "11.111",  // Poprawny PESEL dla osoby urodzonej 02-01-1967
                "1a.23"  // Poprawny PESEL dla osoby urodzonej 02-01-1967
        })
        @DisplayName("Niepoprawne godziny nie powinny przechodzić walidacji")
        void validTextShouldFailValidation(String text) {
            assertFalse(validator.isValid(text, null),
                    "Prawidłowa godzina powinna przejść walidację: " + text);
        }
    }


}