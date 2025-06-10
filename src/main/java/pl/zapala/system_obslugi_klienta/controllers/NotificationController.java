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

@RestController
@RequestMapping("/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    private static final ZoneId WARSAW_ZONE = ZoneId.of("Europe/Warsaw");

    private final NotificationRepository notificationRepo;

    public NotificationController(NotificationRepository notificationRepo) {
        this.notificationRepo = notificationRepo;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<NotificationDto>> getUserNotifications(@PathVariable Integer userId) {
        //List<Notification> nots = notificationRepo.findByUserIdOrderByCreatedAtDesc(userId);
        List<Notification> nots = notificationRepo.findByUserIdOrderByCreatedAtDesc(userId);
        System.out.println("Znalezione powiadomienia: " + nots.size());
        System.out.println("UserId (Powiadomienia) - " + userId);
        List<NotificationDto> dtos = nots.stream()
                .map(this::convertToDto)
                .toList();

        return ResponseEntity.ok(dtos);
    }


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

    @PostMapping("/markAsRead/{id}")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationRepo.findById(id).ifPresent(n -> {
            n.setRead(true);
            notificationRepo.save(n);
        });
        return ResponseEntity.ok().build();
    }

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
