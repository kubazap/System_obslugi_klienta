package pl.zapala.system_obslugi_klienta.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.zapala.system_obslugi_klienta.models.Dokument;
import pl.zapala.system_obslugi_klienta.models.DokumentDto;
import pl.zapala.system_obslugi_klienta.repositories.RepozytoriumDokumentow;

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
    private RepozytoriumDokumentow dokumentyRepo;

    @GetMapping({"", "/"})
    public String getDokumenty(Model model) {
        var dokumenty = dokumentyRepo.findAll();
        model.addAttribute("dokumenty", dokumenty);
        return "dokumenty/index";
    }

    @GetMapping("/listByParent")
    public String getDokumentyByParent(@RequestParam int parentId, Model model) {
        var dokumenty = dokumentyRepo.findAllByParentId(parentId);
        model.addAttribute("dokumenty", dokumenty);
        return "dokumenty/index";
    }

    @GetMapping("/dodaj")
    public String createDokument(Model model) {
        model.addAttribute("dokumentDto", new DokumentDto());
        return "dokumenty/dodaj";
    }

    @PostMapping("/dodaj")
    public String addDokument(@Valid @ModelAttribute("dokumentDto") DokumentDto dokumentDto,
                              BindingResult result,
                              @RequestParam(required = false) MultipartFile file) {

        if (result.hasErrors()) {
            return "dokumenty/dodaj";
        }

        try {
            Dokument dokument = new Dokument();

            dokument.setNazwaDokumentu(dokumentDto.getNazwaDokumentu());
            dokument.setTyp(dokumentDto.getTyp());
            dokument.setUwagi(dokumentDto.getUwagi());
            dokument.setStatus(dokumentDto.getStatus());
            dokument.setParentId(dokumentDto.getParentId());

            java.util.Date utilDate = new java.util.Date();
            java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
            dokument.setDataDodania(sqlDate);

            if (file != null && !file.isEmpty()) {
                String oryginalnaNazwa = file.getOriginalFilename();
                String rozszerzenie = "";
                if (oryginalnaNazwa != null && oryginalnaNazwa.contains(".")) {
                    rozszerzenie = oryginalnaNazwa.substring(oryginalnaNazwa.lastIndexOf('.'));
                }
                String nazwaPliku = System.currentTimeMillis() + rozszerzenie;

                String uploadDir = "storage/dokumenty/";
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                try (InputStream inputStream = file.getInputStream()) {
                    Files.copy(
                            inputStream,
                            uploadPath.resolve(nazwaPliku),
                            StandardCopyOption.REPLACE_EXISTING
                    );
                }

                dokument.setNazwaPliku(nazwaPliku);

            } else {
                dokument.setNazwaPliku(dokumentDto.getNazwaPliku());
            }

            dokumentyRepo.save(dokument);
            dokument.setParentId(dokument.getId());
            dokumentyRepo.save(dokument);

        } catch (Exception ex) {
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
        dokumentDto.setNazwaPliku(dokument.getNazwaPliku());
        dokumentDto.setParentId(dokument.getId());

        List<Dokument> dokumentyPowiazane = dokumentyRepo.findAllByParentId(dokument.getId());

        model.addAttribute("dokument", dokument);
        model.addAttribute("dokumentDto", dokumentDto);
        model.addAttribute("dokumenty", dokumentyPowiazane);

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
            if (dok != null && dok.getParentId() != null) {
                model.addAttribute("dokumenty", dokumentyRepo.findAllByParentId(dok.getParentId()));
            } else {
                model.addAttribute("dokumenty", Collections.emptyList());
            }
            return "dokumenty/edytuj";
        }

        Dokument dokument = dokumentyRepo.findById(id).orElse(null);
        if (dokument == null) {
            return "redirect:/dokumenty";
        }

        try {
            dokument.setNazwaDokumentu(dokumentDto.getNazwaDokumentu());
            dokument.setTyp(dokumentDto.getTyp());
            dokument.setUwagi(dokumentDto.getUwagi());
            dokument.setStatus(dokumentDto.getStatus());
            dokument.setParentId(dokumentDto.getParentId());

            java.util.Date utilDate = new java.util.Date();
            java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
            dokument.setDataDodania(sqlDate);

            if (file != null && !file.isEmpty()) {
                if (dokument.getNazwaPliku() != null) {
                    Path oldFilePath = Paths.get("storage/dokumenty/" + dokument.getNazwaPliku());
                    if (Files.exists(oldFilePath)) {
                        Files.delete(oldFilePath);
                    }
                }

                String oryginalnaNazwa = file.getOriginalFilename();
                String rozszerzenie = "";
                if (oryginalnaNazwa != null && oryginalnaNazwa.contains(".")) {
                    rozszerzenie = oryginalnaNazwa.substring(oryginalnaNazwa.lastIndexOf('.'));
                }
                String nazwaPliku = System.currentTimeMillis() + rozszerzenie;

                String uploadDir = "storage/dokumenty/";
                Path uploadPath = Paths.get(uploadDir);

                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                try (InputStream inputStream = file.getInputStream()) {
                    Files.copy(
                            inputStream,
                            uploadPath.resolve(nazwaPliku),
                            StandardCopyOption.REPLACE_EXISTING
                    );
                }
                dokument.setNazwaPliku(nazwaPliku);
            }

            dokumentyRepo.save(dokument);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return "redirect:/dokumenty";
    }

    @GetMapping("/pobierzPlik")
    public ResponseEntity<Object> pobierzPlik(@RequestParam int dokumentId) {
        try {
            Dokument dokument = dokumentyRepo.findById(dokumentId).orElse(null);
            if (dokument == null) {
                return ResponseEntity.notFound().build();
            }

            File file = new File("storage/dokumenty/" + dokument.getNazwaPliku());
            if (!file.exists()) {
                return ResponseEntity.notFound().build();
            }

            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

            String nazwaPliku = dokument.getNazwaPliku();
            String rozszerzenie = "";
            int dotIndex = nazwaPliku.lastIndexOf('.');
            if (dotIndex > 0) {
                rozszerzenie = nazwaPliku.substring(dotIndex);
            }

            String przyjaznaNazwa = dokument.getNazwaDokumentu() + rozszerzenie;

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + przyjaznaNazwa + "\"")
                    .contentLength(file.length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/usunPlik")
    public String usunPlik(@RequestParam int dokumentId) {
        try {
            Dokument dokument = dokumentyRepo.findById(dokumentId).orElse(null);
            if (dokument == null) {
                return "redirect:/dokumenty";
            }

            Path filePath = Paths.get("storage/dokumenty/" + dokument.getNazwaPliku());
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }

            dokument.setNazwaPliku(null);
            dokumentyRepo.save(dokument);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return "redirect:/dokumenty/edytuj?id=" + dokumentId;
    }

    @GetMapping("/usun")
    public String deleteDokument(@RequestParam("id") int dokumentId) {
        try {
            Dokument dokument = dokumentyRepo.findById(dokumentId).orElse(null);
            if (dokument == null) {
                return "redirect:/dokumenty";
            }

            Path filePath = Paths.get("storage/dokumenty/" + dokument.getNazwaPliku());
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }

            dokumentyRepo.delete(dokument);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return "redirect:/dokumenty";
    }
}
