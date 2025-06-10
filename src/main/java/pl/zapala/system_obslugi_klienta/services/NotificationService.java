package pl.zapala.system_obslugi_klienta.services;

import org.springframework.stereotype.Service;
import pl.zapala.system_obslugi_klienta.models.Notification;
import pl.zapala.system_obslugi_klienta.repositories.NotificationRepository;

import java.time.OffsetDateTime;
import java.time.ZoneId;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepo;
    private static final ZoneId WARSAW_ZONE = ZoneId.of("Europe/Warsaw");

    public NotificationService(NotificationRepository notificationRepo) {
        this.notificationRepo = notificationRepo;
    }

    public void createNotification(Integer userId, String type, String content) {
        Notification n = new Notification();
        n.setUserId(userId);
        n.setType(type);
        n.setContent(content);
        n.setCreatedAt(OffsetDateTime.now(WARSAW_ZONE));
        n.setRead(false);
        notificationRepo.save(n);
    }
}
