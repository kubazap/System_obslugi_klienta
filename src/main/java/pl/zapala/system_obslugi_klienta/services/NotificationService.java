package pl.zapala.system_obslugi_klienta.services;

import org.springframework.stereotype.Service;
import pl.zapala.system_obslugi_klienta.models.Notification;
import pl.zapala.system_obslugi_klienta.repositories.NotificationRepository;

import java.time.OffsetDateTime;
import java.time.ZoneId;

/**
 * Serwis zarządzający tworzeniem powiadomień w systemie.
 * Umożliwia generowanie i zapisywanie powiadomień dla wskazanych użytkowników,
 * z uwzględnieniem czasu utworzenia w strefie Europe/Warsaw.
 */
@Service
public class NotificationService {

    private final NotificationRepository notificationRepo;
    private static final ZoneId WARSAW_ZONE = ZoneId.of("Europe/Warsaw");

    /**
     * Konstruktor serwisu powiadomień.
     *
     * @param notificationRepo repozytorium powiadomień dla operacji CRUD
     */
    public NotificationService(NotificationRepository notificationRepo) {
        this.notificationRepo = notificationRepo;
    }

    /**
     * Tworzy i zapisuje nowe powiadomienie dla użytkownika.
     *
     * @param userId  identyfikator użytkownika, do którego przypisane jest powiadomienie
     * @param type    typ powiadomienia (np. "INFO", "ERROR")
     * @param content treść powiadomienia
     */
    public void createNotification(Integer userId, String type, String content) {
        Notification n = new Notification();
        n.setUserId(userId);
        n.setType(type);
        n.setContent(content);
        // Ustawienie czasu utworzenia w strefie Europe/Warszawa
        n.setCreatedAt(OffsetDateTime.now(WARSAW_ZONE));
        n.setRead(false);
        notificationRepo.save(n);
    }
}