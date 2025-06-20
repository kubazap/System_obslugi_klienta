package pl.zapala.system_obslugi_klienta;

import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
import pl.zapala.system_obslugi_klienta.exception.CodeGenerationFailedException;
import pl.zapala.system_obslugi_klienta.exception.ControllerOperationException;
import pl.zapala.system_obslugi_klienta.exception.EmailSendingException;
import pl.zapala.system_obslugi_klienta.exception.MfaVerificationException;
import pl.zapala.system_obslugi_klienta.models.KlientDto;
import dev.samstevens.totp.exceptions.CodeGenerationException;
import java.sql.Date;
import java.time.LocalDate;
import pl.zapala.system_obslugi_klienta.exception.MfaVerificationException;
import pl.zapala.system_obslugi_klienta.security.MfaExceptionHandler;

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
public class ExceptionsTest {

    private MfaExceptionHandler handler;
    @BeforeEach
    void setUp() {
        handler = new MfaExceptionHandler();
    }
    @Nested
    @DisplayName("Test Email Ex")
    class EmailSendingExceptionTest {

        @Test
        void shouldCreateExceptionWithMessage() {
            EmailSendingException exception = new EmailSendingException("Błąd wysyłania");
            assertEquals("Błąd wysyłania", exception.getMessage());
            assertNull(exception.getCause());
        }

        @Test
        void shouldCreateExceptionWithMessageAndThrowableCause() {
            Throwable cause = new RuntimeException("Przyczyna");
            EmailSendingException exception = new EmailSendingException("Błąd wysyłania", cause);

            assertEquals("Błąd wysyłania", exception.getMessage());
            assertSame(cause, exception.getCause());
        }

        @Test
        void shouldCreateExceptionWithMessageAndMessagingException() {
            MessagingException messagingException = new MessagingException("SMTP error");
            EmailSendingException exception = new EmailSendingException("Nie można wysłać emaila", messagingException);

            assertEquals("Nie można wysłać emaila", exception.getMessage());
            assertSame(messagingException, exception.getCause());
        }
    }
    @Nested
    @DisplayName("Test CodeGenerationFailed")
    class CodeGenerationFailedExceptionTest {

        @Test
        void shouldCreateExceptionWithMessage() {
            CodeGenerationFailedException exception = new CodeGenerationFailedException("Nie można wygenerować kodu");
            assertEquals("Nie można wygenerować kodu", exception.getMessage());
            assertNull(exception.getCause());
        }

        @Test
        void shouldCreateExceptionWithMessageAndThrowableCause() {
            Throwable cause = new RuntimeException("Przyczyna");
            CodeGenerationFailedException exception = new CodeGenerationFailedException("Błąd generowania kodu", cause);

            assertEquals("Błąd generowania kodu", exception.getMessage());
            assertSame(cause, exception.getCause());
        }

        @Test
        void shouldCreateExceptionWithCodeGenerationException() {
            CodeGenerationException totpCause =
                    new CodeGenerationException("Błąd TOTP", new RuntimeException("root cause"));

            CodeGenerationFailedException exception =
                    new CodeGenerationFailedException("Niepowodzenie generowania TOTP", totpCause);

            assertEquals("Niepowodzenie generowania TOTP", exception.getMessage());
            assertSame(totpCause, exception.getCause());
        }
    }
    @Nested
    @DisplayName("Test MfaVerification")
    class MfaVerificationExceptionTest {

        @Test
        void shouldCreateExceptionWithMessage() {
            String message = "Nieprawidłowy kod MFA";
            MfaVerificationException exception = new MfaVerificationException(message);

            assertEquals(message, exception.getMessage());
            assertNull(exception.getCause());
        }

        @Test
        void shouldCreateExceptionWithMessageAndCause() {
            String message = "Weryfikacja MFA nie powiodła się";
            Throwable cause = new RuntimeException("Sesja wygasła");

            MfaVerificationException exception = new MfaVerificationException(message, cause);

            assertEquals(message, exception.getMessage());
            assertSame(cause, exception.getCause());
        }
    }
    @Nested
    @DisplayName("Test ControllerOperation")
    class ControllerOperationExceptionTest {

        @Test
        void shouldCreateExceptionWithMessage() {
            String message = "Błąd wykonania operacji w kontrolerze";
            ControllerOperationException exception = new ControllerOperationException(message);

            assertEquals(message, exception.getMessage());
            assertNull(exception.getCause());
        }

        @Test
        void shouldCreateExceptionWithMessageAndCause() {
            String message = "Nie udało się zakończyć operacji HTTP";
            Throwable cause = new RuntimeException("Błąd połączenia z serwerem");

            ControllerOperationException exception = new ControllerOperationException(message, cause);

            assertEquals(message, exception.getMessage());
            assertSame(cause, exception.getCause());
        }
    }
    @Nested
    @DisplayName("Test MfaExceptionHandler")
    class MfaExceptionHandlerTest {
    @Test
    void shouldRedirectToTimeoutWhenSessionExpired() {
        RedirectAttributes attrs = new RedirectAttributesModelMap();
        MfaVerificationException ex = new MfaVerificationException("Sesja wygasła. Zaloguj się ponownie.");

        String view = handler.handleMfaException(ex, attrs);

        assertEquals("redirect:/login?timeout", view);
        assertEquals("Sesja wygasła. Zaloguj się ponownie.", attrs.getFlashAttributes().get("mfaErrorMessage"));
    }

    @Test
    void shouldRedirectToMfaErrorWhenMessageDoesNotContainTimeout() {
        RedirectAttributes attrs = new RedirectAttributesModelMap();
        MfaVerificationException ex = new MfaVerificationException("Kod MFA jest nieprawidłowy.");

        String view = handler.handleMfaException(ex, attrs);

        assertEquals("redirect:/login?mfa&error", view);
        assertEquals("Kod MFA jest nieprawidłowy.", attrs.getFlashAttributes().get("mfaErrorMessage"));
    }
}
}
