package pl.zapala.system_obslugi_klienta.controllers;

import jakarta.validation.Valid;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.zapala.system_obslugi_klienta.exception.ControllerOperationException;
import pl.zapala.system_obslugi_klienta.exception.StorageException;
import pl.zapala.system_obslugi_klienta.exception.InvalidFileTypeException;
import pl.zapala.system_obslugi_klienta.exception.VirusFoundException;
import pl.zapala.system_obslugi_klienta.models.Dokument;
import pl.zapala.system_obslugi_klienta.models.DokumentDto;
import pl.zapala.system_obslugi_klienta.models.Plik;
import pl.zapala.system_obslugi_klienta.models.Pracownik;
import pl.zapala.system_obslugi_klienta.repositories.DokumentRepository;
import pl.zapala.system_obslugi_klienta.repositories.PlikRepository;
import pl.zapala.system_obslugi_klienta.repositories.PracownikRepository;
import pl.zapala.system_obslugi_klienta.services.AntivirusService;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.sql.Date;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/dokumenty")
public class DokumentController {
    private static final String STORAGE_DOKUMENTY_DIR = "storage/dokumenty/";
    private static final String REDIRECT_DOKUMENTY = "redirect:/dokumenty";
    private static final String REDIRECT_EDIT_DOKUMENT = "redirect:/dokumenty/edytuj?id=";
    private static final String REDIRECT_ADD_DOKUMENT = "dokumenty/dodaj";
    private static final String ATTR_PLIKI = "pliki";
    private static final String ATTR_DOKUMENT = "dokument";
    private static final String ATTR_DOKUMENT_DTO = "dokumentDto";

    private final DokumentRepository dokumentyRepo;
    private final PlikRepository plikiRepo;
    private final PracownikRepository pracownikRepo;
    private final AntivirusService antivirusService;

    public DokumentController(DokumentRepository dokumentyRepo,
                              PlikRepository plikiRepo,
                              PracownikRepository pracownikRepo,
                              AntivirusService antivirusService) {
        this.dokumentyRepo    = dokumentyRepo;
        this.plikiRepo        = plikiRepo;
        this.pracownikRepo    = pracownikRepo;
        this.antivirusService = antivirusService;
    }

    @ModelAttribute
    public void loggedPracownik(Model model) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()
                && !(auth instanceof AnonymousAuthenticationToken)) {

            String email = auth.getName();
            Pracownik pracownik = pracownikRepo.findByEmail(email);
            model.addAttribute("pracownik", pracownik);
        }
    }

    @GetMapping({"", "/"})
    public String getDokumenty(Model model) {
        List<Dokument> dokumenty = dokumentyRepo.findAll();
        model.addAttribute("dokumenty", dokumenty);
        return "dokumenty/index";
    }

    @GetMapping("/dodaj")
    public String createDokument(Model model) {
        model.addAttribute(ATTR_DOKUMENT_DTO, new DokumentDto());
        return REDIRECT_ADD_DOKUMENT;
    }

    @PostMapping("/dodaj")
    public String addDokumentWithPlik(
            @Valid @ModelAttribute(ATTR_DOKUMENT_DTO) DokumentDto dokumentDto,
            BindingResult result,
            @RequestParam(required = false) MultipartFile file, Model model) {

        if (result.hasErrors()) {
            return REDIRECT_ADD_DOKUMENT;
        }
        try {
            if (file != null && !file.isEmpty()) {
                validatePdf(file);
                antivirusService.scan(file.getBytes());
            }

            Dokument dokument = new Dokument();
            dokument.setNazwaDokumentu(dokumentDto.getNazwaDokumentu());
            dokument.setTyp(dokumentDto.getTyp());
            dokument.setUwagi(dokumentDto.getUwagi());
            dokument.setStatus(dokumentDto.getStatus());
            dokument.setDataDodania(new Date(System.currentTimeMillis()));
            dokumentyRepo.save(dokument);

            long currentTime = System.currentTimeMillis();
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
            }
            String storageFileName = currentTime + extension;

            Path uploadPath = Paths.get(STORAGE_DOKUMENTY_DIR);
            Files.createDirectories(uploadPath);
            try (InputStream is = file.getInputStream()) {
                Files.copy(is, uploadPath.resolve(storageFileName), StandardCopyOption.REPLACE_EXISTING);
            }

            Plik plik = new Plik();
            plik.setDataDodania(new Date(currentTime));
            plik.setNazwaPliku(storageFileName);
            plik.setDokument(dokument);
            dokument.getPliki().add(plik);
            dokumentyRepo.save(dokument);
        } catch (InvalidFileTypeException | VirusFoundException ex) {
            model.addAttribute("fileError", ex.getMessage());
            return REDIRECT_ADD_DOKUMENT;
        }
        catch (Exception ex) {
            throw new ControllerOperationException("Nie udało się dodać dokumentu", ex);
        }
        return REDIRECT_DOKUMENTY;
    }

    @GetMapping("/edytuj")
    public String editDokument(@RequestParam int id, Model model) {
        Dokument dokument = dokumentyRepo.findById(id).orElse(null);
        if (dokument == null) {
            return REDIRECT_DOKUMENTY;
        }

        DokumentDto dokumentDto = new DokumentDto();
        dokumentDto.setNazwaDokumentu(dokument.getNazwaDokumentu());
        dokumentDto.setTyp(dokument.getTyp());
        dokumentDto.setUwagi(dokument.getUwagi());
        dokumentDto.setStatus(dokument.getStatus());
        dokumentDto.setDataDodania(dokument.getDataDodania());

        model.addAttribute(ATTR_DOKUMENT, dokument);
        model.addAttribute(ATTR_DOKUMENT_DTO, dokumentDto);
        model.addAttribute(ATTR_PLIKI, dokument.getPliki());

        return "dokumenty/edytuj";
    }

    @PostMapping("/edytuj")
    public String updateDokument(Model model,
                                 @RequestParam int id,
                                 @Valid @ModelAttribute(ATTR_DOKUMENT_DTO) DokumentDto dokumentDto,
                                 BindingResult result,
                                 @RequestParam(required = false) MultipartFile file) {
        if (result.hasErrors()) {
            Dokument dok = dokumentyRepo.findById(id).orElse(null);
            if (dok != null) {
                model.addAttribute(ATTR_PLIKI, dok.getPliki());
            } else {
                model.addAttribute(ATTR_PLIKI, Collections.emptyList());
            }
            return REDIRECT_EDIT_DOKUMENT + id;
        }

        Dokument dokument = dokumentyRepo.findById(id).orElse(null);
        if (dokument == null) {
            return REDIRECT_EDIT_DOKUMENT + id;
        }
        try {
            dokument.setNazwaDokumentu(dokumentDto.getNazwaDokumentu());
            dokument.setTyp(dokumentDto.getTyp());
            dokument.setUwagi(dokumentDto.getUwagi());
            dokument.setStatus(dokumentDto.getStatus());
            dokument.setDataDodania(new Date(System.currentTimeMillis()));

            dokumentyRepo.save(dokument);
        } catch (Exception ex) {
            throw new ControllerOperationException("Nie udało się zaktualizować dokumentu", ex);
        }
        return REDIRECT_DOKUMENTY;
    }

    @PostMapping("/edytuj/dodajPlik")
    public String addAnotherPlikToDokument(@RequestParam int id,
                                           @RequestParam MultipartFile file,
                                           RedirectAttributes redirect) {

        if (file == null || file.isEmpty()) {
            return REDIRECT_EDIT_DOKUMENT + id;
        }
        Dokument dokument = dokumentyRepo.findById(id).orElse(null);
        if (dokument == null) {
            return REDIRECT_DOKUMENTY;
        }

        try {
            validatePdf(file);
            antivirusService.scan(file.getBytes());

            long currentTime = System.currentTimeMillis();
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
            }
            String storageFileName = currentTime + extension;

            Path uploadPath = Paths.get(STORAGE_DOKUMENTY_DIR);
            Files.createDirectories(uploadPath);
            try (InputStream is = file.getInputStream()) {
                Files.copy(is, uploadPath.resolve(storageFileName),
                        StandardCopyOption.REPLACE_EXISTING);
            }

            Plik plik = new Plik();
            plik.setDataDodania(new Date(currentTime));
            plik.setNazwaPliku(storageFileName);
            plik.setDokument(dokument);
            dokument.getPliki().add(plik);
            dokumentyRepo.save(dokument);

        } catch (InvalidFileTypeException | VirusFoundException ex) {
            redirect.addFlashAttribute("fileError", ex.getMessage());
        } catch (Exception ex) {
            throw new ControllerOperationException("Nie udało się dodać pliku do dokumentu", ex);
        }
        return REDIRECT_EDIT_DOKUMENT + id;
    }

    @GetMapping("/pobierzPlik")
    public ResponseEntity<Object> pobierzPlik(@RequestParam int fileId) {
        try {
            Plik plik = plikiRepo.findById(fileId).orElse(null);
            if (plik == null) {
                return ResponseEntity.notFound().build();
            }
            File file = new File(STORAGE_DOKUMENTY_DIR, plik.getNazwaPliku());
            if (!file.exists()) {
                return ResponseEntity.notFound().build();
            }
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                    .contentLength(file.length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (Exception ex) {
            throw new ControllerOperationException("Nie udało się pobrać pliku", ex);
        }
    }

    @GetMapping("/usunPlik")
    public String usunPlik(@RequestParam int documentId, @RequestParam int fileId) {
        try {
            Plik plik = plikiRepo.findById(fileId).orElse(null);
            if (plik != null) {
                Path filePath = Paths.get(STORAGE_DOKUMENTY_DIR)
                        .resolve(plik.getNazwaPliku());
                Files.deleteIfExists(filePath);
                plikiRepo.delete(plik);
            }
        } catch (StorageException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ControllerOperationException("Nie udało się usunąć pliku", ex);
        }
        return REDIRECT_EDIT_DOKUMENT + documentId;
    }

    @GetMapping("/usun")
    public String deleteDokument(@RequestParam("id") int dokumentId) {
        try {
            Dokument dokument = dokumentyRepo.findById(dokumentId)
                    .orElseThrow(() -> new ControllerOperationException(
                            "Nie znaleziono dokumentu o id=" + dokumentId));

            deleteAssociatedFiles(dokument);
            dokumentyRepo.delete(dokument);

        } catch (StorageException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ControllerOperationException("Nie udało się usunąć dokumentu", ex);
        }
        return REDIRECT_DOKUMENTY;
    }

    private void deleteAssociatedFiles(Dokument dokument) {
        if (dokument.getPliki() == null) {
            return;
        }
        for (Plik plik : dokument.getPliki()) {
            deleteFile(plik.getNazwaPliku());
        }
    }

    private void deleteFile(String fileName) {
        try {
            Path filePath = Paths.get(STORAGE_DOKUMENTY_DIR)
                    .resolve(fileName);
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            throw new StorageException("Nie udało się usunąć pliku: " + fileName, ex);
        }
    }

    private void validatePdf(MultipartFile file) throws IOException {
        if (!"application/pdf".equalsIgnoreCase(file.getContentType())) {
            throw new InvalidFileTypeException("Dozwolony typ pliku: PDF");
        }
        try (InputStream is = file.getInputStream()) {
            byte[] head = is.readNBytes(5);
            if (head.length < 5 || !"%PDF-".equals(new String(head, StandardCharsets.US_ASCII))) {
                throw new InvalidFileTypeException("Dozwolony typ pliku: PDF");
            }
        }
    }

}