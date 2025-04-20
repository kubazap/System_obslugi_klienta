package pl.zapala.system_obslugi_klienta.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
import pl.zapala.system_obslugi_klienta.models.Dokument;
import pl.zapala.system_obslugi_klienta.models.DokumentDto;
import pl.zapala.system_obslugi_klienta.models.Plik;
import pl.zapala.system_obslugi_klienta.models.Pracownik;
import pl.zapala.system_obslugi_klienta.repositories.DokumentRepository;
import pl.zapala.system_obslugi_klienta.repositories.PlikRepository;
import pl.zapala.system_obslugi_klienta.repositories.PracownikRepository;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.*;
import java.sql.Date;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/dokumenty")
public class DokumentController {
    @Autowired
    private DokumentRepository dokumentyRepo;
    @Autowired
    private PlikRepository plikiRepo;
    @Autowired
    private PracownikRepository pracownikRepo;

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
        model.addAttribute("dokumentDto", new DokumentDto());
        return "dokumenty/dodaj";
    }

    @PostMapping("/dodaj")
    public String addDokumentWithPlik(@Valid @ModelAttribute("dokumentDto") DokumentDto dokumentDto,
                                      BindingResult result,
                                      @RequestParam(required = false) MultipartFile file) {
        if(result.hasErrors()){
            return "dokumenty/dodaj";
        }
        try {
            Dokument dokument = new Dokument();
            dokument.setNazwaDokumentu(dokumentDto.getNazwaDokumentu());
            dokument.setTyp(dokumentDto.getTyp());
            dokument.setUwagi(dokumentDto.getUwagi());
            dokument.setStatus(dokumentDto.getStatus());
            dokument.setDataDodania(new Date(System.currentTimeMillis()));
            dokumentyRepo.save(dokument);

            if (file != null && !file.isEmpty()){
                long currentTime = System.currentTimeMillis();
                String originalFilename = file.getOriginalFilename();
                String extension = "";
                if (originalFilename != null && originalFilename.contains(".")) {
                    extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
                }
                String storageFileName = currentTime + extension;
                String uploadDir = "storage/dokumenty/";
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                try (InputStream inputStream = file.getInputStream()) {
                    Files.copy(inputStream, uploadPath.resolve(storageFileName),
                            StandardCopyOption.REPLACE_EXISTING);
                }
                Plik plik = new Plik();
                plik.setDataDodania(new Date(currentTime));
                plik.setNazwaPliku(storageFileName);
                plik.setDokument(dokument);
                dokument.getPliki().add(plik);
                dokumentyRepo.save(dokument);
            }
        } catch(Exception ex){
            ex.printStackTrace();
        }
        return "redirect:/dokumenty";
    }

    @GetMapping("/edytuj")
    public String editDokument(@RequestParam int id, Model model) {
        Dokument dokument = dokumentyRepo.findById(id).orElse(null);
        if (dokument == null) {
            return "redirect:/dokumenty";
        }

        DokumentDto dokumentDto = new DokumentDto();
        dokumentDto.setNazwaDokumentu(dokument.getNazwaDokumentu());
        dokumentDto.setTyp(dokument.getTyp());
        dokumentDto.setUwagi(dokument.getUwagi());
        dokumentDto.setStatus(dokument.getStatus());
        dokumentDto.setDataDodania(dokument.getDataDodania());

        model.addAttribute("dokument", dokument);
        model.addAttribute("dokumentDto", dokumentDto);
        model.addAttribute("pliki", dokument.getPliki());

        return "dokumenty/edytuj";
    }

    @PostMapping("/edytuj")
    public String updateDokument(Model model,
                                 @RequestParam int id,
                                 @Valid @ModelAttribute("dokumentDto") DokumentDto dokumentDto,
                                 BindingResult result,
                                 @RequestParam(required = false) MultipartFile file) {
        if (result.hasErrors()) {
            Dokument dok = dokumentyRepo.findById(id).orElse(null);
            if (dok != null) {
                model.addAttribute("pliki", dok.getPliki());
            } else {
                model.addAttribute("pliki", Collections.emptyList());
            }
            return "redirect:/dokumenty/edytuj?id=" + id;
        }

        Dokument dokument = dokumentyRepo.findById(id).orElse(null);
        if (dokument == null) {
            return "redirect:/dokumenty/edytuj?id=" + id;
        }
        try {
            dokument.setNazwaDokumentu(dokumentDto.getNazwaDokumentu());
            dokument.setTyp(dokumentDto.getTyp());
            dokument.setUwagi(dokumentDto.getUwagi());
            dokument.setStatus(dokumentDto.getStatus());
            dokument.setDataDodania(new Date(System.currentTimeMillis()));

            dokumentyRepo.save(dokument);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "redirect:/dokumenty";
    }

    @PostMapping("/edytuj/dodajPlik")
    public String addAnotherPlikToDokument(@RequestParam int id,
                                    @RequestParam MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                return "redirect:/dokumenty/edytuj?id=" + id;
            }
            Dokument dokument = dokumentyRepo.findById(id).orElse(null);
            if (dokument == null) {
                return "redirect:/dokumenty/";
            }

            long currentTime = System.currentTimeMillis();
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
            }
            String storageFileName = currentTime + extension;
            String uploadDir = "storage/dokumenty/";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, uploadPath.resolve(storageFileName),
                        StandardCopyOption.REPLACE_EXISTING);
            }

            Plik plik = new Plik();
            plik.setDataDodania(new Date(currentTime));
            plik.setNazwaPliku(storageFileName);
            plik.setDokument(dokument);

            dokument.getPliki().add(plik);
            dokumentyRepo.save(dokument);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "redirect:/dokumenty/edytuj?id=" + id;
    }

    @GetMapping("/pobierzPlik")
    public ResponseEntity<Object> pobierzPlik(@RequestParam int fileId) {
        try {
            Plik plik = plikiRepo.findById(fileId).orElse(null);
            if (plik == null) {
                return ResponseEntity.notFound().build();
            }
            File file = new File("storage/dokumenty/" + plik.getNazwaPliku());
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
            ex.printStackTrace();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/usunPlik")
    public String usunPlik(@RequestParam int documentId, @RequestParam int fileId) {
        try {
            Plik plik = plikiRepo.findById(fileId).orElse(null);
            if (plik != null) {
                Path filePath = Paths.get("storage/dokumenty/" + plik.getNazwaPliku());
                Files.deleteIfExists(filePath);
                plikiRepo.delete(plik);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "redirect:/dokumenty/edytuj?id=" + documentId;
    }

    @GetMapping("/usun")
    public String deleteDokument(@RequestParam("id") int dokumentId) {
        try {
            Dokument dokument = dokumentyRepo.findById(dokumentId).orElse(null);
            if (dokument == null) {
                return "redirect:/dokumenty";
            }
            if (dokument.getPliki() != null) {
                for (Plik plik : dokument.getPliki()) {
                    try {
                        Path filePath = Paths.get("storage/dokumenty/" + plik.getNazwaPliku());
                        Files.deleteIfExists(filePath);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
            dokumentyRepo.delete(dokument);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "redirect:/dokumenty";
    }
}