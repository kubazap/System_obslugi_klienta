package pl.zapala.system_obslugi_klienta;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
import pl.zapala.system_obslugi_klienta.controllers.DokumentController;
import pl.zapala.system_obslugi_klienta.exception.ControllerOperationException;
import pl.zapala.system_obslugi_klienta.exception.InvalidFileTypeException;
import pl.zapala.system_obslugi_klienta.exception.StorageException;
import pl.zapala.system_obslugi_klienta.exception.VirusFoundException;
import pl.zapala.system_obslugi_klienta.models.Dokument;
import pl.zapala.system_obslugi_klienta.models.DokumentDto;
import pl.zapala.system_obslugi_klienta.models.Plik;
import pl.zapala.system_obslugi_klienta.models.Pracownik;
import pl.zapala.system_obslugi_klienta.repositories.DokumentRepository;
import pl.zapala.system_obslugi_klienta.repositories.PlikRepository;
import pl.zapala.system_obslugi_klienta.repositories.PracownikRepository;
import pl.zapala.system_obslugi_klienta.services.AntivirusService;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import static org.mockito.Mockito.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
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
class DokumentControllerTest {

    @Autowired
    private DokumentController dokumentController;

    @Autowired
    private DokumentRepository dokumentRepository;

    @Autowired
    private PlikRepository plikRepository;

    private DokumentDto dokumentDto;

    @BeforeEach
    void setUp() {
        dokumentRepository.deleteAll();
        plikRepository.deleteAll();

        dokumentDto = new DokumentDto();
        dokumentDto.setNazwaDokumentu("Umowa");
        dokumentDto.setTyp("PDF");
        dokumentDto.setUwagi("Kluczowy dokument");
        dokumentDto.setStatus(true);
    }

    @Nested
    @DisplayName("Operacje na dokumentach")
    class DokumentTests {
        @Test
        @DisplayName("Dodawanie dokumentu z plikiem")
        void shouldAddDokumentSuccessfully() throws IOException {

            AntivirusService antivirusServiceMock = mock(AntivirusService.class);
            DokumentController dokumentController = new DokumentController(
                    dokumentRepository,
                    mock(pl.zapala.system_obslugi_klienta.repositories.PlikRepository.class),
                    mock(pl.zapala.system_obslugi_klienta.repositories.PracownikRepository.class),
                    antivirusServiceMock
            );

            BindingResult result = new BeanPropertyBindingResult(dokumentDto, "dokumentDto");
            ExtendedModelMap model = new ExtendedModelMap();

            byte[] pdfContent = "%PDF-1.4 sample".getBytes(StandardCharsets.US_ASCII);
            MockMultipartFile mockFile = new MockMultipartFile(
                    "file",
                    "plik_testowy.pdf",
                    "application/pdf",
                    pdfContent
            );

            String viewName = dokumentController.addDokumentWithPlik(dokumentDto, result, mockFile, model);

            assertEquals("redirect:/dokumenty", viewName);

            List<Dokument> wszystkie = dokumentRepository.findAll();
            assertEquals(1, wszystkie.size());

            Dokument added = wszystkie.get(0);
            assertEquals("Umowa", added.getNazwaDokumentu());
            assertEquals("PDF", added.getTyp());
            assertTrue(added.getStatus());
            assertEquals("Kluczowy dokument", added.getUwagi());

            verify(antivirusServiceMock).scan((byte[]) any());
            dokumentController.loggedPracownik(model);
        }

        @Test
        @DisplayName("Dodawanie dokumentu z plikiem - exception1")
        void shouldThrowExAddingDocument() throws IOException {
            AntivirusService antivirusServiceMock = mock(AntivirusService.class);

            doThrow(new VirusFoundException("Wirus wykryty")).when(antivirusServiceMock).scan((byte[]) any());

            DokumentController dokumentController = new DokumentController(
                    dokumentRepository,
                    mock(pl.zapala.system_obslugi_klienta.repositories.PlikRepository.class),
                    mock(pl.zapala.system_obslugi_klienta.repositories.PracownikRepository.class),
                    antivirusServiceMock
            );

            DokumentDto dokumentDto = new DokumentDto();
            dokumentDto.setNazwaDokumentu("Umowa");
            dokumentDto.setTyp("PDF");
            dokumentDto.setStatus(true);
            dokumentDto.setUwagi("Kluczowy dokument");

            BindingResult result = new BeanPropertyBindingResult(dokumentDto, "dokumentDto");
            ExtendedModelMap model = new ExtendedModelMap();

            byte[] pdfContent = "%PDF-1.4 sample".getBytes(StandardCharsets.US_ASCII);
            MockMultipartFile mockFile = new MockMultipartFile(
                    "file",
                    "plik_testowy.pdf",
                    "application/pdf",
                    pdfContent
            );

            String viewName = dokumentController.addDokumentWithPlik(dokumentDto, result, mockFile, model);
            assertEquals("dokumenty/dodaj", viewName);
            assertTrue(model.containsAttribute("fileError"));
            assertEquals("Wirus wykryty", model.get("fileError"));
        }

        @DisplayName("Dodawanie kolejnego pliku - exception1")
        @Test
        void shouldThrowExAddingAnotherFile() throws IOException {
            AntivirusService antivirusServiceMock = mock(AntivirusService.class);
            PlikRepository plikRepositoryMock = mock(PlikRepository.class);
            PracownikRepository pracownikRepositoryMock = mock(PracownikRepository.class);

            doThrow(new VirusFoundException("Plik zawiera wirusa")).when(antivirusServiceMock).scan((byte[]) any());

            DokumentController dokumentController = new DokumentController(
                    dokumentRepository,
                    plikRepositoryMock,
                    pracownikRepositoryMock,
                    antivirusServiceMock
            );

            Dokument dokument = new Dokument();
            dokument = dokumentRepository.save(dokument);

            byte[] pdfContent = "%PDF-1.4 test".getBytes(StandardCharsets.US_ASCII);
            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    "plik.pdf",
                    "application/pdf",
                    pdfContent
            );

            RedirectAttributes redirect = new RedirectAttributesModelMap();

            String viewName = dokumentController.addAnotherPlikToDokument(dokument.getId(), file, redirect);

            assertEquals("redirect:/dokumenty/edytuj?id=" + dokument.getId(), viewName);
            assertTrue(redirect.getFlashAttributes().containsKey("fileError"));
            assertEquals("Plik zawiera wirusa", redirect.getFlashAttributes().get("fileError"));
        }

        @Test
        @DisplayName("Edycja dokumentu")
        void shouldEditDokumentSuccessfully() {
            Dokument dokument = new Dokument();
            dokument.setNazwaDokumentu("Stary");
            dokument.setTyp("TXT");
            dokument.setStatus(false);
            dokument.setUwagi("Stare dane");
            dokument.setDataDodania(new Date(System.currentTimeMillis()));
            dokument = dokumentRepository.save(dokument);

            DokumentDto updateDto = new DokumentDto();
            updateDto.setNazwaDokumentu("Nowy");
            updateDto.setTyp("DOCX");
            updateDto.setStatus(true);
            updateDto.setUwagi("Zaktualizowany");
            updateDto.setDataDodania(new Date(System.currentTimeMillis()));

            BindingResult result = new BeanPropertyBindingResult(updateDto, "dokumentDto");

            ExtendedModelMap model = new ExtendedModelMap();
            String viewName = dokumentController.updateDokument(model, dokument.getId(), updateDto, result, null);

            assertEquals("redirect:/dokumenty", viewName);

            Dokument updated = dokumentRepository.findById(dokument.getId()).orElseThrow();
            assertEquals("Nowy", updated.getNazwaDokumentu());
            assertEquals("DOCX", updated.getTyp());
            assertTrue(updated.getStatus());
            assertEquals("Zaktualizowany", updated.getUwagi());
        }

        @Test
        @DisplayName("Powinien usunąć dokument z plikami")
        void shouldDeleteDokumentWithFilesSuccessfully() {

            Dokument dokument = new Dokument();
            dokument.setNazwaDokumentu("Testowy dokument");
            dokument.setDataDodania(new Date(System.currentTimeMillis()));
            dokument.setStatus(false);
            dokument.setTyp("PDF");
            dokument.setUwagi("Uwagi testowe");


            Plik plik = new Plik();
            plik.setNazwaPliku("plik1.pdf");
            plik.setDataDodania(new Date(System.currentTimeMillis()));
            plik.setDokument(dokument);

            List<Plik> pliki = new ArrayList<>();
            pliki.add(plik);
            dokument.setPliki(pliki);

            dokument = dokumentRepository.save(dokument);


            dokument = dokumentRepository.findById(dokument.getId()).orElseThrow();
            dokument.getPliki().size();


            String viewName = dokumentController.deleteDokument(dokument.getId());


            assertEquals("redirect:/dokumenty", viewName);
            assertFalse(dokumentRepository.findById(dokument.getId()).isPresent());
        }

        @DisplayName("Usuwanie dokumentu - storage exception")
        @Test
        void deleteDokument_shouldThrowStorageException() {
            final Dokument dokument = new Dokument();
            dokument.setId(1);

            DokumentRepository dokumentyRepoMock = mock(DokumentRepository.class);
            when(dokumentyRepoMock.findById(dokument.getId())).thenReturn(Optional.of(dokument));
            doThrow(new StorageException("Storage error")).when(dokumentyRepoMock).delete(dokument);

            DokumentController controller = new DokumentController(
                    dokumentyRepoMock,
                    mock(PlikRepository.class),
                    mock(PracownikRepository.class),
                    mock(AntivirusService.class)
            );

            StorageException thrown = assertThrows(StorageException.class, () -> {
                controller.deleteDokument(dokument.getId());
            });

            assertEquals("Storage error", thrown.getMessage());
        }
        @DisplayName("Usuwanie dokumentu - operation exception")
        @Test
        void deleteDokument_shouldThrowControllerOperationException() {
            final Dokument dokument = new Dokument();
            dokument.setId(1);

            DokumentRepository dokumentyRepoMock = mock(DokumentRepository.class);
            when(dokumentyRepoMock.findById(dokument.getId())).thenReturn(Optional.of(dokument));
            doThrow(new RuntimeException("Unexpected error")).when(dokumentyRepoMock).delete(dokument);

            DokumentController controller = new DokumentController(
                    dokumentyRepoMock,
                    mock(PlikRepository.class),
                    mock(PracownikRepository.class),
                    mock(AntivirusService.class)
            );

            ControllerOperationException thrown = assertThrows(ControllerOperationException.class, () -> {
                controller.deleteDokument(dokument.getId());
            });

            assertTrue(thrown.getMessage().contains("Nie udało się usunąć dokumentu"));
            assertTrue(thrown.getCause() instanceof RuntimeException);
            assertEquals("Unexpected error", thrown.getCause().getMessage());
        }

        @DisplayName("Wyświetelenie dokumentów")
        @Test
        void getDokumenty_shouldReturnDokumenty() {
            Dokument dokument = new Dokument();
            dokument.setNazwaDokumentu("Test");
            dokument = dokumentRepository.save(dokument);

            ExtendedModelMap model = new ExtendedModelMap();
            String viewName = dokumentController.getDokumenty(model);

            assertEquals("dokumenty/index", viewName);
            assertTrue(((List<?>)model.getAttribute("dokumenty")).contains(dokument));
        }

        @DisplayName("Tworzenie dokumentu - EmptyDto")
        @Test
        void createDokument_shouldReturnEmptyDto() {
            ExtendedModelMap model = new ExtendedModelMap();
            String viewName = dokumentController.createDokument(model);

            assertEquals("dokumenty/dodaj", viewName);
            assertNotNull(model.getAttribute("dokumentDto"));
        }
        @DisplayName("Dodawanie dokumentu z plikiem - ValidationErrors")
        @Test
        void addDokumentWithPlik_shouldReturnErrors() {
            BindingResult result = mock(BindingResult.class);
            when(result.hasErrors()).thenReturn(true);

            ExtendedModelMap model = new ExtendedModelMap();

            String viewName = dokumentController.addDokumentWithPlik(dokumentDto, result, null, model);

            assertEquals("dokumenty/dodaj", viewName);
        }

        @DisplayName("Edycja dokumentu poprawna")
        @Test
        void editDokument_shouldReturnEditView() {
            Dokument dokument = new Dokument();
            dokument.setNazwaDokumentu("Test");
            dokument = dokumentRepository.save(dokument);

            ExtendedModelMap model = new ExtendedModelMap();
            String viewName = dokumentController.editDokument(dokument.getId(), model);

            assertEquals("dokumenty/edytuj", viewName);
            assertNotNull(model.getAttribute("dokument"));
            assertNotNull(model.getAttribute("dokumentDto"));
            assertNotNull(model.getAttribute("pliki"));
        }
        @DisplayName("Edycja dokumentu - NotFound")
        @Test
        void editDokument_shouldRedirectIfDokumentNotFound() {
            ExtendedModelMap model = new ExtendedModelMap();
            String viewName = dokumentController.editDokument(-1, model);
            assertEquals("redirect:/dokumenty", viewName);
        }
        @DisplayName("Aktualizacja dokumentu - HasErrors")
        @Test
        void updateDokument_shouldReturnEditRedirectOnErrors() {
            BindingResult result = mock(BindingResult.class);
            when(result.hasErrors()).thenReturn(true);

            ExtendedModelMap model = new ExtendedModelMap();

            Dokument dokument = new Dokument();
            dokument = dokumentRepository.save(dokument);

            String viewName = dokumentController.updateDokument(model, dokument.getId(), dokumentDto, result, null);

            assertEquals("redirect:/dokumenty/edytuj?id=" + dokument.getId(), viewName);
            assertNotNull(model.getAttribute("pliki"));
        }
        @DisplayName("Aktualizacja dokumentu - NotFound")
        @Test
        void updateDokument_shouldRedirectIfDokumentNotFound() {
            BindingResult result = mock(BindingResult.class);
            when(result.hasErrors()).thenReturn(false);

            ExtendedModelMap model = new ExtendedModelMap();

            String viewName = dokumentController.updateDokument(model, -1, dokumentDto, result, null);

            assertEquals("redirect:/dokumenty/edytuj?id=-1", viewName);
        }
        @DisplayName("Dodanie kolejnego pliku poprawne")
        @Test
        void addAnotherPlikToDokument_shouldAddFileSuccessfully() throws IOException {
            AntivirusService antivirusServiceMock = mock(AntivirusService.class);
            PlikRepository mockPlikRepo = mock(PlikRepository.class);
            PracownikRepository mockPracownikRepo = mock(PracownikRepository.class);

            DokumentController dokumentController = new DokumentController(
                    dokumentRepository,
                    mockPlikRepo,
                    mockPracownikRepo,
                    antivirusServiceMock
            );

            Dokument dokument = new Dokument();
            dokument = dokumentRepository.save(dokument);

            byte[] pdfContent = "%PDF-1.4 sample".getBytes(StandardCharsets.US_ASCII);
            MockMultipartFile file = new MockMultipartFile("file", "plik.pdf", "application/pdf", pdfContent);

            RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();

            doNothing().when(antivirusServiceMock).scan(any(byte[].class));

            String viewName = dokumentController.addAnotherPlikToDokument(dokument.getId(), file, redirectAttributes);

            assertEquals("redirect:/dokumenty/edytuj?id=" + dokument.getId(), viewName);
            Dokument updated = dokumentRepository.findById(dokument.getId()).orElseThrow();
            assertFalse(updated.getPliki().isEmpty());

            verify(antivirusServiceMock).scan(any(byte[].class));
        }

        @DisplayName("Dodanie kolejnego pliku - FileEmpty")
        @Test
        void addAnotherPlikToDokument_shouldRedirectIfFileEmpty() {
            RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();

            String viewName = dokumentController.addAnotherPlikToDokument(1, null, redirectAttributes);
            assertEquals("redirect:/dokumenty/edytuj?id=1", viewName);
        }

        @DisplayName("Dodanie kolejnego pliku - NotFound")
        @Test
        void addAnotherPlikToDokument_shouldRedirectIfDokumentNotFound() throws IOException {
            byte[] pdfContent = "%PDF-1.4 sample".getBytes(StandardCharsets.US_ASCII);
            MockMultipartFile file = new MockMultipartFile("file", "plik.pdf", "application/pdf", pdfContent);

            RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();

            String viewName = dokumentController.addAnotherPlikToDokument(-1, file, redirectAttributes);
            assertEquals("redirect:/dokumenty", viewName);
        }
        @DisplayName("Pobierz plik poprawne")
        @Test
        void pobierzPlik_shouldReturnFile() throws IOException {
            Dokument dokument = new Dokument();
            dokument = dokumentRepository.save(dokument);

            Plik plik = new Plik();
            plik.setDokument(dokument);
            plik.setNazwaPliku("plik.pdf");
            plik.setDataDodania(new java.sql.Date(System.currentTimeMillis()));
            plik = plikRepository.save(plik);

            Path storagePath = Paths.get("storage/dokumenty");
            Files.createDirectories(storagePath);
            Path filePath = storagePath.resolve("plik.pdf");
            Files.write(filePath, "%PDF-1.4".getBytes(StandardCharsets.US_ASCII));

            ResponseEntity<Object> response = dokumentController.pobierzPlik(plik.getId());

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());

            if (response.getBody() instanceof InputStreamResource) {
                try (InputStream is = ((InputStreamResource) response.getBody()).getInputStream()) {
                    is.readAllBytes();
                }
            }

            String redirect = dokumentController.usunPlik(dokument.getId(), plik.getId());
            assertEquals("redirect:/dokumenty/edytuj?id=" + dokument.getId(), redirect);
            assertFalse(Files.exists(filePath), "Plik fizyczny powinien być usunięty");
            assertFalse(plikRepository.findById(plik.getId()).isPresent(), "Plik w bazie powinien być usunięty");
        }

        @DisplayName("Usun plik - StorageException")
        @Test
        void usunPlik_shouldThrowStorageException() throws IOException {
            final Dokument dokument = new Dokument();
            dokumentRepository.save(dokument);

            final Plik plik = new Plik();
            plik.setDokument(dokument);
            plik.setNazwaPliku("plik.pdf");
            plik.setDataDodania(new java.sql.Date(System.currentTimeMillis()));
            plikRepository.save(plik);

            PlikRepository plikRepoMock = mock(PlikRepository.class);
            when(plikRepoMock.findById(plik.getId())).thenReturn(Optional.of(plik));
            doThrow(new StorageException("Storage error")).when(plikRepoMock).delete(any());

            DokumentController controller = new DokumentController(
                    dokumentRepository,
                    plikRepoMock,
                    mock(PracownikRepository.class),
                    mock(AntivirusService.class)
            );

            StorageException thrown = assertThrows(StorageException.class, () -> {
                controller.usunPlik(dokument.getId(), plik.getId());
            });

            assertEquals("Storage error", thrown.getMessage());
        }

        @DisplayName("Usun plik - OperationException")
        @Test
        void usunPlik_shouldThrowControllerOperationException() throws IOException {
            final Dokument dokument = new Dokument();
            dokumentRepository.save(dokument);

            final Plik plik = new Plik();
            plik.setDokument(dokument);
            plik.setNazwaPliku("plik.pdf");
            plik.setDataDodania(new java.sql.Date(System.currentTimeMillis()));
            plikRepository.save(plik);

            PlikRepository plikRepoMock = mock(PlikRepository.class);
            when(plikRepoMock.findById(plik.getId())).thenReturn(Optional.of(plik));
            doThrow(new RuntimeException("Unexpected error")).when(plikRepoMock).delete(any());

            DokumentController controller = new DokumentController(
                    dokumentRepository,
                    plikRepoMock,
                    mock(PracownikRepository.class),
                    mock(AntivirusService.class)
            );

            ControllerOperationException thrown = assertThrows(ControllerOperationException.class, () -> {
                controller.usunPlik(dokument.getId(), plik.getId());
            });

            assertTrue(thrown.getMessage().contains("Nie udało się usunąć pliku"));
            assertTrue(thrown.getCause() instanceof RuntimeException);
            assertEquals("Unexpected error", thrown.getCause().getMessage());
        }

        @DisplayName("Pobierz plik - Exception")
        @Test
        void pobierzPlik_shouldReturnEx() throws IOException {
            Dokument dokument = new Dokument();
            dokument = dokumentRepository.save(dokument);

            Plik plik = new Plik();
            plik.getDataDodania();
            plik.getDokument();
            plik.setDokument(dokument);
            plik.setNazwaPliku("plik123.pdf");
            plik.setDataDodania(new java.sql.Date(System.currentTimeMillis()));
            plik = plikRepository.save(plik);

            ResponseEntity<Object> response = dokumentController.pobierzPlik(plik.getId());
            plik.setId(1);
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertNull(response.getBody());


        }
        @DisplayName("Pobierz plik - NotFound")
        @Test
        void pobierzPlik_shouldReturnNotFound() {
            ResponseEntity<Object> response = dokumentController.pobierzPlik(-1);
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        }

        @DisplayName("Walidacja PDF - HEAD < 5 bytes")
        @Test
        void validatePdf_shouldThrowLessThan5Bytes() throws Exception {
            byte[] shortContent = "abc".getBytes();
            MultipartFile file = new MockMultipartFile(
                    "file", "plik.pdf", "application/pdf", new ByteArrayInputStream(shortContent)
            );

            InvocationTargetException thrown = assertThrows(InvocationTargetException.class, () -> {
                invokeValidatePdf(file);
            });

            Throwable cause = thrown.getCause();
            assertTrue(cause instanceof InvalidFileTypeException);
            assertEquals("Dozwolony typ pliku: PDF", cause.getMessage());
        }


        private void invokeValidatePdf(MultipartFile file) throws Exception {

            var method = DokumentController.class.getDeclaredMethod("validatePdf", MultipartFile.class);
            method.setAccessible(true);
            method.invoke(dokumentController, file);
        }
        @Test
        void loggedPracownik_shouldAddLoggedUserToModel_whenAuthenticated() {
            // Mock Authentication
            Authentication authentication = mock(Authentication.class);
            when(authentication.isAuthenticated()).thenReturn(true);
            when(authentication.getName()).thenReturn("jan@example.com");

            // Ustaw SecurityContext
            SecurityContext securityContext = mock(SecurityContext.class);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            SecurityContextHolder.setContext(securityContext);

            // Mock PracownikRepository
            PracownikRepository pracownikRepo = mock(PracownikRepository.class);
            Pracownik expectedPracownik = new Pracownik();
            expectedPracownik.setEmail("jan@example.com");
            when(pracownikRepo.findByEmail("jan@example.com")).thenReturn(expectedPracownik);

            // Mock Model
            Model model = mock(Model.class);

            // Stwórz instancję kontrolera
            DokumentController controller = new DokumentController(
                    mock(DokumentRepository.class),
                    mock(PlikRepository.class),
                    pracownikRepo,
                    mock(AntivirusService.class)
            );

            // Wywołaj metodę
            controller.loggedPracownik(model);

            // Sprawdź, czy pracownik został dodany do modelu
            verify(model).addAttribute("pracownik", expectedPracownik);
        }
    }
}