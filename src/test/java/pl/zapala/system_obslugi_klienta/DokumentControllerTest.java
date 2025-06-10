package pl.zapala.system_obslugi_klienta;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import pl.zapala.system_obslugi_klienta.controllers.DokumentController;
import pl.zapala.system_obslugi_klienta.models.Dokument;
import pl.zapala.system_obslugi_klienta.models.DokumentDto;
import pl.zapala.system_obslugi_klienta.models.Plik;
import pl.zapala.system_obslugi_klienta.repositories.DokumentRepository;
import pl.zapala.system_obslugi_klienta.services.AntivirusService;

import static org.mockito.Mockito.*;
import java.io.*;

import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;


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

    private DokumentDto dokumentDto;

    @BeforeEach
    void setUp() {
        dokumentRepository.deleteAll();

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

            byte[] pdfContent = "%PDF-1.4 sample".getBytes(StandardCharsets.US_ASCII); // poprawny nagłówek PDF
            MockMultipartFile mockFile = new MockMultipartFile(
                    "file",
                    "plik_testowy.pdf",
                    "application/pdf",
                    pdfContent
            );

            // when
            String viewName = dokumentController.addDokumentWithPlik(dokumentDto, result, mockFile, model);

            // then
            assertEquals("redirect:/dokumenty", viewName);

            List<Dokument> wszystkie = dokumentRepository.findAll();
            assertEquals(1, wszystkie.size());

            Dokument added = wszystkie.get(0);
            assertEquals("Umowa", added.getNazwaDokumentu());
            assertEquals("PDF", added.getTyp());
            assertTrue(added.getStatus());
            assertEquals("Kluczowy dokument", added.getUwagi());

            // verify antivirus scan was triggered
            verify(antivirusServiceMock).scan((byte[]) any());
        }

        @Test
        @DisplayName("Edycja dokumentu")
        void shouldEditDokumentSuccessfully() {
            // Dodaj dokument testowy
            Dokument dokument = new Dokument();
            dokument.setNazwaDokumentu("Stary");
            dokument.setTyp("TXT");
            dokument.setStatus(false);
            dokument.setUwagi("Stare dane");
            dokument.setDataDodania(new Date(System.currentTimeMillis()));
            dokument = dokumentRepository.save(dokument);

            // Zaktualizowane dane
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
            // przygotowanie dokumentu
            Dokument dokument = new Dokument();
            dokument.setNazwaDokumentu("Testowy dokument");
            dokument.setDataDodania(new Date(System.currentTimeMillis()));
            dokument.setStatus(false);
            dokument.setTyp("PDF");
            dokument.setUwagi("Uwagi testowe");

            // przygotowanie pliku
            Plik plik = new Plik();
            plik.setNazwaPliku("plik1.pdf");
            plik.setDataDodania(new Date(System.currentTimeMillis()));
            plik.setDokument(dokument); // ustawienie relacji

            List<Plik> pliki = new ArrayList<>();
            pliki.add(plik);
            dokument.setPliki(pliki); // ustawienie relacji

            dokument = dokumentRepository.save(dokument); // zapis dokumentu razem z plikiem

            // wymuszenie inicjalizacji plików, żeby Hibernate nie rzucał LazyInitializationException
            dokument = dokumentRepository.findById(dokument.getId()).orElseThrow();
            dokument.getPliki().size(); // inicjalizacja kolekcji

            // wykonanie
            String viewName = dokumentController.deleteDokument(dokument.getId());

            // asercje
            assertEquals("redirect:/dokumenty", viewName);
            assertFalse(dokumentRepository.findById(dokument.getId()).isPresent());
        }
    }
}