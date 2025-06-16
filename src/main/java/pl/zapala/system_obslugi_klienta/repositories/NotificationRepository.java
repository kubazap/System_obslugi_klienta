package pl.zapala.system_obslugi_klienta.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.zapala.system_obslugi_klienta.models.Notification;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Pobiera listę powiadomień dla danego użytkownika, posortowaną malejąco według daty utworzenia.
     *
     * @param userId identyfikator użytkownika, dla którego pobierane są powiadomienia
     * @return lista powiadomień użytkownika w kolejności od najnowszego do najstarszego
     */
    List<Notification> findByUserIdOrderByCreatedAtDesc(Integer userId);
}
