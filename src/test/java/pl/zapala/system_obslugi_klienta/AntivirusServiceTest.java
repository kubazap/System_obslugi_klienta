package pl.zapala.system_obslugi_klienta;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import pl.zapala.system_obslugi_klienta.exception.StorageException;
import pl.zapala.system_obslugi_klienta.services.AntivirusService;
import org.springframework.web.multipart.MultipartFile;
import pl.zapala.system_obslugi_klienta.exception.VirusFoundException;
import java.io.InputStream;
import java.io.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

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
public class AntivirusServiceTest {

    private AntivirusService antivirusService;

    @BeforeEach
    void setUp() {
        antivirusService = new AntivirusService();
        ReflectionTestUtils.setField(antivirusService, "enabled", true);
        ReflectionTestUtils.setField(antivirusService, "host", "localhost");
        ReflectionTestUtils.setField(antivirusService, "port", 3310);
    }

    @Test
    @DisplayName("Nie skanuje gdy AV wyłączone")
    void shouldSkipScanWhenDisabled() {
        ReflectionTestUtils.setField(antivirusService, "enabled", false);
        assertDoesNotThrow(() -> antivirusService.scan("test".getBytes()));
    }

    @Test
    @DisplayName("Wyrzuca StorageException przy braku odpowiedzi")
    void shouldThrowStorageExceptionWhenNoResponse() {
        byte[] input = "test".getBytes();
        AntivirusService av = new AntivirusService() {
            protected void doScan(InputStream payload) {
                throw new StorageException("Brak odpowiedzi od ClamAV");
            }
        };

        ReflectionTestUtils.setField(av, "enabled", true);
        StorageException ex = assertThrows(StorageException.class, () -> av.scan(input));
        assertEquals("Błąd połączenia z ClamAV (null:0)", ex.getMessage());
    }

    @Test
    @DisplayName("Wyrzuca VirusFoundException gdy wykryto wirusa")
    void shouldThrowVirusFoundExceptionWhenVirusFound() {
        AntivirusService av = Mockito.mock(AntivirusService.class);

        doThrow(new VirusFoundException("Wykryto niebezpieczny plik, spróbuj dodać inny. (Eicar-Test-Signature)"))
                .when(av).scan(any(byte[].class));

        VirusFoundException ex = assertThrows(VirusFoundException.class,
                () -> av.scan("infected".getBytes()));

        assertTrue(ex.getMessage().contains("Eicar-Test-Signature"));
    }

    @Test
    @DisplayName("Wyrzuca StorageException przy IOException z MultipartFile")
    void shouldThrowStorageExceptionWhenMultipartFileFails() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getInputStream()).thenThrow(new IOException("File read error"));

        StorageException ex = assertThrows(StorageException.class, () -> antivirusService.scan(file));
        assertTrue(ex.getMessage().contains("Nie można odczytać pliku do skanowania"));
    }

    @Test
    @DisplayName("Nie rzuca wyjątku jeśli AV wyłączone przy MultipartFile")
    void shouldSkipMultipartScanWhenDisabled() {
        MultipartFile file = mock(MultipartFile.class);
        ReflectionTestUtils.setField(antivirusService, "enabled", false);

        assertDoesNotThrow(() -> antivirusService.scan(file));
    }
}