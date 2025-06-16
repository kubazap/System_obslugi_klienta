package pl.zapala.system_obslugi_klienta.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.zapala.system_obslugi_klienta.models.Notification;
import pl.zapala.system_obslugi_klienta.models.NotificationDto;
import pl.zapala.system_obslugi_klienta.repositories.NotificationRepository;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Kontroler udostępniający API do zarządzania powiadomieniami użytkowników.
 * Umożliwia pobranie listy powiadomień, tworzenie nowych oraz oznaczanie ich jako odczytane.
 */
@RestController
@RequestMapping("/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    private static final ZoneId WARSAW_ZONE = ZoneId.of("Europe/Warsaw");

    private final NotificationRepository notificationRepo;

    /**
     * Tworzy instancję NotificationController.
     *
     * @param notificationRepo repozytorium do operacji na encjach Notification
     */
    public NotificationController(NotificationRepository notificationRepo) {
        this.notificationRepo = notificationRepo;
    }

    /**
     * Pobiera wszystkie powiadomienia danego użytkownika, posortowane malejąco po dacie utworzenia.
     *
     * @param userId identyfikator użytkownika, dla którego pobierane są powiadomienia
     * @return ResponseEntity zawierające listę obiektów NotificationDto
     */
    @GetMapping("/{userId}")
    public ResponseEntity<List<NotificationDto>> getUserNotifications(@PathVariable Integer userId) {
        List<Notification> nots = notificationRepo.findByUserIdOrderByCreatedAtDesc(userId);
        System.out.println("Znalezione powiadomienia: " + nots.size());
        System.out.println("UserId (Powiadomienia) - " + userId);
        List<NotificationDto> dtos = nots.stream()
                .map(this::convertToDto)
                .toList();

        return ResponseEntity.ok(dtos);
    }

    /**
     * Tworzy nowe powiadomienie na podstawie danych z DTO.
     *
     * @param dto obiekt NotificationDto zawierający dane nowego powiadomienia
     * @return ResponseEntity z zapisaną encją Notification
     */
    @PostMapping
    public ResponseEntity<Notification> createNotification(@RequestBody NotificationDto dto) {
        Notification notification = new Notification();
        notification.setUserId(dto.getUserId());
        notification.setType(dto.getType());
        notification.setContent(dto.getContent());
        notification.setCreatedAt(OffsetDateTime.now(WARSAW_ZONE));
        notification.setRead(false);

        return ResponseEntity.ok(notificationRepo.save(notification));
    }

    /**
     * Oznacza powiadomienie o podanym identyfikatorze jako odczytane.
     *
     * @param id identyfikator powiadomienia do oznaczenia jako odczytane
     * @return ResponseEntity z kodem 200 OK
     */
    @PostMapping("/markAsRead/{id}")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationRepo.findById(id).ifPresent(n -> {
            n.setRead(true);
            notificationRepo.save(n);
        });
        return ResponseEntity.ok().build();
    }

    /**
     * Konwertuje encję Notification na DTO NotificationDto.
     *
     * @param n encja powiadomienia
     * @return wypełniony obiekt NotificationDto
     */
    private NotificationDto convertToDto(Notification n) {
        NotificationDto dto = new NotificationDto();
        dto.setId(n.getId());
        dto.setUserId(n.getUserId());
        dto.setType(n.getType());
        dto.setContent(n.getContent());
        dto.setCreatedAt(n.getCreatedAt() != null ? n.getCreatedAt().format(FORMATTER) : null);
        dto.setRead(n.getRead());
        return dto;
    }
}