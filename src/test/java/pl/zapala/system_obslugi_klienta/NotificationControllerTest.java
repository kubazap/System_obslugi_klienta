package pl.zapala.system_obslugi_klienta;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import pl.zapala.system_obslugi_klienta.controllers.NotificationController;
import pl.zapala.system_obslugi_klienta.models.*;
import pl.zapala.system_obslugi_klienta.repositories.NotificationRepository;
import pl.zapala.system_obslugi_klienta.repositories.PracownikRepository;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
class NotificationControllerTest {

    @Autowired
    private NotificationController notificationController;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private PracownikRepository pracownikRepository;
    private Notification notification;
    private NotificationDto notificationDto;
    private Pracownik pracownik;

    @BeforeEach
    void setUp() {
        notificationRepository.deleteAll();
        notificationRepository.deleteAllInBatch();
        pracownikRepository.deleteAll();

        pracownik = new Pracownik();
        pracownik.setImie("Jan");
        pracownik.setNazwisko("Kowalski");
        pracownikRepository.save(pracownik);

        notification = new Notification();
        notification.setUserId(pracownik.getId());
        notification.setType("Wiadomość");
        notification.setContent("Nowa wiadomość");
        ZoneId WARSAW_ZONE = ZoneId.of("Europe/Warsaw");
        notification.setCreatedAt(OffsetDateTime.now(WARSAW_ZONE));
        notification.setRead(false);
        notificationRepository.save(notification);
    }

    @Nested
    @DisplayName("Operacje na powiadomieniach")
    class NotificationControllerTests {

        @Test
        @DisplayName("Tworzenie powiadomień")
        void CreateNotification() {
            notificationDto = new NotificationDto();
            notificationDto.setId(1L);
            notificationDto.setUserId(pracownik.getId());
            notificationDto.setType("Wiadomość");
            notificationDto.setContent("Nowa wiadomość");
            ZoneId WARSAW_ZONE = ZoneId.of("Europe/Warsaw");

            notificationDto.setRead(false);

            ResponseEntity<Notification> response = notificationController.createNotification(notificationDto);
            notification = new Notification();
            notification = response.getBody();
            long id = notificationDto.getId();
            assertEquals(notification.getUserId(),notificationDto.getUserId());
            assertEquals(notification.getType(),notificationDto.getType());
            assertEquals(notification.getContent(),notificationDto.getContent());
            assertEquals(notification.getRead(),notificationDto.isRead());
        }

        @Test
        @DisplayName("Zmienianie statusu odczytanej wiadomości")
        void MarkAsRead() {
            ResponseEntity<Void> response = notificationController.markAsRead(notification.getId());
            List<Notification> notifications = notificationRepository.findAll();
            assertTrue(notifications.get(0).getRead());
        }

        @Test
        @DisplayName("Lista powiadomien użytkownika")
        void ListNotifications() {
            notificationDto = new NotificationDto();
            notificationDto.setId(1L);
            notificationDto.setUserId(pracownik.getId());
            notificationDto.setType("Wiadomość");
            notificationDto.setContent("Nowa wiadomość");
            ZoneId WARSAW_ZONE = ZoneId.of("Europe/Warsaw");
            notificationDto.setRead(false);

            NotificationDto notificationDto1 = new NotificationDto();
            notificationDto1.setId(2L);
            notificationDto1.setUserId(pracownik.getId());
            notificationDto1.setType("Wiadomość12");
            notificationDto1.setContent("Nowa wiadomość12");
            notificationDto1.setRead(false);

            notificationController.createNotification(notificationDto);
            notificationController.createNotification(notificationDto1);

            ResponseEntity<List<NotificationDto>> notificationsEntity =
                    notificationController.getUserNotifications(pracownik.getId());
            List<NotificationDto> notificationsList = notificationsEntity.getBody();

            assertEquals(notificationsList.get(0).getContent(),notificationDto1.getContent());
            assertEquals(notificationsList.get(1).getContent(),notificationDto.getContent());
        }

    }

}