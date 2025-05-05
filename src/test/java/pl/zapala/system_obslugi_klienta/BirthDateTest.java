package pl.zapala.system_obslugi_klienta;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import pl.zapala.system_obslugi_klienta.validators.BirthDateValidator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Date;
import java.time.LocalDate;
import java.util.stream.Stream;

@SpringBootTest
class BirthDateTest {
    private BirthDateValidator validator;
    @BeforeEach
    void setUp() {
        validator = new BirthDateValidator();
    }

    @Nested
    @DisplayName("Valid Birth Date")
    class ValidDateTests {

        static Stream<Date> validDates() {
            return Stream.of(
                    Date.valueOf(LocalDate.of(1944, 5, 14)),
                    Date.valueOf(LocalDate.of(2002, 7, 8)),
                    Date.valueOf(LocalDate.of(1990, 9, 5)),
                    Date.valueOf(LocalDate.of(1980, 12, 17)),
                    Date.valueOf(LocalDate.of(1967, 1, 2))
            );
        }

        @ParameterizedTest
        @MethodSource("validDates")
        @DisplayName("Poprawne daty powinny przechodzić walidację")
        void validDatesShouldPassValidation(Date date) {
            assertTrue(validator.isValid(date, null),
                    "Data powinna przejść walidację: " + date);
        }
    }

    @Nested
    @DisplayName("Invalid Birth Date")
    class InvalidDateTests {

        static Stream<Date> invalidDates() {
            return Stream.of(
                    Date.valueOf(LocalDate.of(2028, 11, 14)),
                    Date.valueOf(LocalDate.of(3002, 7, 31)),
                    Date.valueOf(LocalDate.of(1910, 9, 5)),
                    Date.valueOf(LocalDate.of(980, 12, 17)),
                    Date.valueOf(LocalDate.of(967, 1, 2))
            );
        }

        @ParameterizedTest
        @MethodSource("invalidDates")
        @DisplayName("Niepoprawne daty nie powinny przechodzić walidacji")
        void invalidDatesShouldFailValidation(Date date) {
            assertFalse(validator.isValid(date, null),
                    "Nieprawidłowa data powinna nie przejść walidacji: " + date);
        }
    }

}