package pl.zapala.system_obslugi_klienta;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import pl.zapala.system_obslugi_klienta.validators.*;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Date;
import java.time.LocalDate;
import java.util.stream.Stream;

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
class ValidatorsTest {
    private BirthDateValidator birthDateValidator;
    private EmailValidator emailValidator;
    private NameValidator nameValidator;
    private PostalValidator postalValidator;
    private TimeValidator timeValidator;
    private VisitDateValidator visitDateValidator;
    private PhoneValidator phoneValidator;
    private MoneyValidator moneyValidator;
    private RoomValidator roomValidator;
    private StreetValidator streetValidator;

    @BeforeEach
    void setUp() {
        birthDateValidator = new BirthDateValidator();
        emailValidator = new EmailValidator();
        nameValidator = new NameValidator();
        postalValidator = new PostalValidator();
        timeValidator = new TimeValidator();
        visitDateValidator = new VisitDateValidator();
        phoneValidator = new PhoneValidator();
        moneyValidator = new MoneyValidator();
        roomValidator = new RoomValidator();
        streetValidator = new StreetValidator();
    }

    @Nested
    @DisplayName("Birth Date Validation")
    class BirthDateValidationTests {

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
            assertTrue(birthDateValidator.isValid(date, null),
                    "Data powinna przejść walidację: " + date);
        }

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
        @DisplayName("Niepoprawne daty powinny przechodzić walidację")
        void invalidDatesShouldFailValidation(Date date) {
            assertFalse(birthDateValidator.isValid(date, null),
                    "Nieprawidłowa data powinna nie przejść walidacji: " + date);
        }
    }
    @Nested
    @DisplayName("Mail Validation")
    class MailValidationTests {
        @ParameterizedTest
        @ValueSource(strings = {
                "kacper@gmail.com",
                "jakub@o2.pl",
                "maciek@for.com",
                "Juan-Martin@xd.pl",
                "qweqwcq@asd.fr"
        })
        @DisplayName("Poprawne maile powinny przechodzić walidację")
        void validTextShouldPassValidation(String text) {
            assertTrue(emailValidator.isValid(text, null),
                    "Prawidłowy mail powinien przejść walidację: " + text);
        }


        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {
                "@gmail.com",
                "kuba@gmail.",
                "kuba@.com",
                "a@g.c",
                "qweqwcq.com"
        })
        @DisplayName("Niepoprawne maile nie powinny przechodzić walidacji")
        void validTextShouldFailValidation(String text) {
            assertFalse(emailValidator.isValid(text, null), "Prawidłowy mail powinien przejść walidację: " + text);
        }

    }
    @Nested
    @DisplayName("Text Name Validation")
    class TextValidationTests {

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
            assertTrue(nameValidator.isValid(text, null),
                    "Prawidłowy PESEL powinien przejść walidację: " + text);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {
                "123", // Poprawny PESEL dla osoby urodzonej 14-05-1944
                "Jakub1", // Poprawny PESEL dla osoby urodzonej 08-07-2002
                "...", // Poprawny PESEL dla osoby urodzonej 05-09-1990
                "????", // Poprawny PESEL dla osoby urodzonej 17-12-1980
                "qweqwcq.com"  // Poprawny PESEL dla osoby urodzonej 02-01-1967
        })
        @DisplayName("Niepoprawne teksty nie powinny przechodzić walidacji")
        void validTextShouldFailValidation(String text) {
            assertFalse(nameValidator.isValid(text, null),
                    "Prawidłowy PESEL powinien przejść walidację: " + text);
        }
    }
    @Nested
    @DisplayName("Postal Code Validation")
    class PostalValidationTests {

        @ParameterizedTest
        @ValueSource(strings = {
                "12-123",
                "92-532",
                "10-100",
        })
        @DisplayName("Poprawne kody powinny przechodzić walidację")
        void validTextShouldPassValidation(String text) {
            assertTrue(postalValidator.isValid(text, null),
                    "Prawidłowy kod powinien przejść walidację: " + text);
        }


        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {
                "12 123", // Poprawny PESEL dla osoby urodzonej 14-05-1944
                "2-132", // Poprawny PESEL dla osoby urodzonej 08-07-2002
                "12-12", // Poprawny PESEL dla osoby urodzonej 05-09-1990
                "12-141a", // Poprawny PESEL dla osoby urodzonej 17-12-1980
                "23-a21"  // Poprawny PESEL dla osoby urodzonej 02-01-1967
        })
        @DisplayName("Niepoprawne kody nie powinny przechodzić walidacji")
        void validTextShouldFailValidation(String text) {
            assertFalse(postalValidator.isValid(text, null),
                    "Prawidłowy kod powinien przejść walidację: " + text);
        }
    }

    @Nested
    @DisplayName("Time Validation")
    class TimeValidationTests {

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
            assertTrue(timeValidator.isValid(text, null),
                    "Prawidłowa godzina powinna przejść walidację: " + text);
        }

        @ParameterizedTest
        @NullAndEmptySource
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
            assertFalse(timeValidator.isValid(text, null),
                    "Prawidłowa godzina powinna przejść walidację: " + text);
        }
    }

    @Nested
    @DisplayName("Visit Date Validation")
    class VisitDateValidationTests {

        static Stream<Date> validDates() {
            return Stream.of(
                    Date.valueOf(LocalDate.of(2025, 6, 30)),
                    Date.valueOf(LocalDate.of(2026, 7, 8)),
                    Date.valueOf(LocalDate.of(2027, 9, 5)),
                    Date.valueOf(LocalDate.of(2030, 12, 17)),
                    Date.valueOf(LocalDate.of(2029, 1, 2))
            );
        }

        @ParameterizedTest
        @MethodSource("validDates")
        @DisplayName("Poprawne daty powinny przechodzić walidację")
        void validDatesShouldPassValidation(Date date) {
            assertTrue(visitDateValidator.isValid(date, null),
                    "Data powinna przejść walidację: " + date);
        }

        static Stream<Date> invalidDates() {
            return Stream.of(
                    Date.valueOf(LocalDate.of(2025, 5, 4)),
                    Date.valueOf(LocalDate.of(2025, 1, 1)),
                    Date.valueOf(LocalDate.of(1910, 9, 5)),
                    Date.valueOf(LocalDate.of(980, 12, 17)),
                    Date.valueOf(LocalDate.of(967, 1, 2))
            );
        }

        @ParameterizedTest
        @MethodSource("invalidDates")
        @DisplayName("Niepoprawne daty nie powinny przechodzić walidacji")
        void invalidDatesShouldFailValidation(Date date) {
            assertFalse(visitDateValidator.isValid(date, null),
                    "Nieprawidłowa data powinna nie przejść walidacji: " + date);
        }
    }
    @Nested
    @DisplayName("Money Validation")
    class MoneyTests {

        @ParameterizedTest
        @ValueSource(strings = {
                "12 zł",
                "123 eur",
                "123 usd",
        })
        @DisplayName("Poprawne ilosci powinny przechodzić walidację")
        void validTextShouldPassValidation(String text) {
            assertTrue(moneyValidator.isValid(text, null),
                    "Prawidłowy kod powinien przejść walidację: " + text);
        }


        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {
                "12",
                "",
                "123 pesos",
        })
        @DisplayName("Niepoprawne ilosci nie powinny przechodzić walidacji")
        void validTextShouldFailValidation(String text) {
            assertFalse(moneyValidator.isValid(text, null),
                    "Prawidłowy kod powinien przejść walidację: " + text);
        }
    }
    @Nested
    @DisplayName("Phone Validation")
    class PhoneTests {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {
                "+48 123 456 789",
                "+1-800-1234",
                "",
        })
        @DisplayName("Poprawne ilosci powinny przechodzić walidację")
        void validTextShouldPassValidation(String text) {
            assertTrue(phoneValidator.isValid(text, null),
                    "Prawidłowy kod powinien przejść walidację: " + text);
        }


        @ParameterizedTest
        @ValueSource(strings = {
                "axd xdd xxx",
                "123 123 123",
                "+12;,",
        })
        @DisplayName("Niepoprawne ilosci nie powinny przechodzić walidacji")
        void validTextShouldFailValidation(String text) {
            assertFalse(phoneValidator.isValid(text, null),
                    "Prawidłowy kod powinien przejść walidację: " + text);
        }
    }
    @Nested
    @DisplayName("Room Validation")
    class RoomTests {

        @ParameterizedTest
        @ValueSource(strings = {
                "1.12 DH",
                "12",
                "Sala 24",
        })
        @DisplayName("Poprawne ilosci powinny przechodzić walidację")
        void validTextShouldPassValidation(String text) {
            assertTrue(roomValidator.isValid(text, null),
                    "Prawidłowy kod powinien przejść walidację: " + text);
        }


        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {
                "Sala $",
                "sala 1/2",
                "",
        })
        @DisplayName("Niepoprawne ilosci nie powinny przechodzić walidacji")
        void validTextShouldFailValidation(String text) {
            assertFalse(roomValidator.isValid(text, null),
                    "Prawidłowy kod powinien przejść walidację: " + text);
        }
    }

    @Nested
    @DisplayName("Street Validation")
    class StreetTests {

        @ParameterizedTest
        @ValueSource(strings = {
                "Słoneczna 2, Kielce",
                "Piekoszów 21",
                "Kielecka 2/12 ",
        })
        @DisplayName("Poprawne ilosci powinny przechodzić walidację")
        void validTextShouldPassValidation(String text) {
            assertTrue(streetValidator.isValid(text, null),
                    "Prawidłowy kod powinien przejść walidację: " + text);
        }


        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {
                "   ",       // tylko spacje
                "{Kielecka}",
                "1=1",
        })
        @DisplayName("Niepoprawne ilosci nie powinny przechodzić walidacji")
        void validTextShouldFailValidation(String text) {
            assertFalse(streetValidator.isValid(text, null),
                    "Prawidłowy kod powinien przejść walidację: " + text);
        }
    }
}