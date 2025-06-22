package pl.zapala.system_obslugi_klienta;

import dev.samstevens.totp.code.CodeVerifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import pl.zapala.system_obslugi_klienta.controllers.MfaController;
import pl.zapala.system_obslugi_klienta.models.Pracownik;
import pl.zapala.system_obslugi_klienta.repositories.PracownikRepository;
import static org.mockito.Mockito.*;
import java.time.Instant;
import jakarta.servlet.http.HttpSession;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import pl.zapala.system_obslugi_klienta.exception.MfaVerificationException;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Instant;

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
class MfaControllerTest {

    private CodeVerifier verifier;
    private PracownikRepository repo;
    private MfaController controller;

    @BeforeEach
    void setUp() {
        verifier = mock(CodeVerifier.class);
        repo = mock(PracownikRepository.class);
        controller = new MfaController(verifier, repo);
    }
    @Nested
    @DisplayName("GET /login?mfa")
    class MfaFormTests {

        @Test
        @DisplayName("Wyświetla formularz MFA z pozostałym czasem")
        void shouldShowMfaFormWithRemainingTime() {
            MockHttpSession session = new MockHttpSession();
            session.setAttribute("MFA_START", Instant.now().getEpochSecond());

            Model model = new ConcurrentModel();
            String view = controller.mfaForm(session, model, null);

            assertEquals("logowanie/mfa", view);
            assertTrue((Long) model.getAttribute("remainingSeconds") > 0);
            assertNull(model.getAttribute("error"));
        }

        @Test
        @DisplayName("Zwraca wyjątek, gdy sesja MFA nie została zainicjowana")
        void shouldThrowIfNoMfaStart() {
            HttpSession session = mock(HttpSession.class);
            when(session.getAttribute("MFA_START")).thenReturn(null);

            Model model = new ConcurrentModel();
            assertThrows(MfaVerificationException.class, () ->
                    controller.mfaForm(session, model, null));
        }

        @Test
        @DisplayName("Zwraca wyjątek, gdy sesja MFA wygasła")
        void shouldThrowIfMfaSessionExpired() {
            MockHttpSession session = new MockHttpSession();
            session.setAttribute("MFA_START", Instant.now().minusSeconds(200).getEpochSecond());

            Model model = new ConcurrentModel();
            assertThrows(MfaVerificationException.class, () ->
                    controller.mfaForm(session, model, null));
        }

        @Test
        @DisplayName("Dodaje atrybut błędu do modelu")
        void shouldIncludeErrorIfProvided() {
            MockHttpSession session = new MockHttpSession();
            session.setAttribute("MFA_START", Instant.now().getEpochSecond());

            Model model = new ConcurrentModel();
            String view = controller.mfaForm(session, model, "true");

            assertEquals("logowanie/mfa", view);
            assertEquals(true, model.getAttribute("error"));
        }
    }

    @Nested
    @DisplayName("POST /login?mfa")
    class VerifyTests {

        @Test
        @DisplayName("Poprawny kod – przekierowanie do /wizyty")
        void shouldVerifyCodeAndRedirect() {
            MockHttpSession session = new MockHttpSession();
            session.setAttribute("EMAIL", "user@example.com");

            Pracownik pracownik = new Pracownik();
            pracownik.setTotpSecret("SECRET");
            when(repo.findByEmail("user@example.com")).thenReturn(pracownik);
            when(verifier.isValidCode("SECRET", "123456")).thenReturn(true);

            String result = controller.verify("123456", session);
            assertEquals("redirect:/wizyty", result);
            assertTrue((Boolean) session.getAttribute("MFA_PASSED"));
        }

        @Test
        @DisplayName("Brak emaila – wyjątek")
        void shouldThrowIfNoEmail() {
            HttpSession session = mock(HttpSession.class);
            when(session.getAttribute("EMAIL")).thenReturn(null);

            assertThrows(MfaVerificationException.class, () ->
                    controller.verify("123456", session));
        }

        @Test
        @DisplayName("Nieprawidłowy kod – wyjątek")
        void shouldThrowIfCodeInvalid() {
            MockHttpSession session = new MockHttpSession();
            session.setAttribute("EMAIL", "user@example.com");

            Pracownik pracownik = new Pracownik();
            pracownik.setTotpSecret("SECRET");
            when(repo.findByEmail("user@example.com")).thenReturn(pracownik);
            when(verifier.isValidCode("SECRET", "wrong")).thenReturn(false);

            assertThrows(MfaVerificationException.class, () ->
                    controller.verify("wrong", session));
        }
    }
}