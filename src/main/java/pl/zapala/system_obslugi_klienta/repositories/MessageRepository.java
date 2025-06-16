package pl.zapala.system_obslugi_klienta.repositories;

import pl.zapala.system_obslugi_klienta.models.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    /**
     * Pobiera całą konwersację między dwoma użytkownikami, posortowaną rosnąco według czasu wysłania.
     *
     * @param user1 identyfikator pierwszego uczestnika rozmowy
     * @param user2 identyfikator drugiego uczestnika rozmowy
     * @return lista wiadomości wymienionych między user1 a user2
     */
    @Query("SELECT m FROM Message m WHERE " +
            "(m.senderId = :user1 AND m.receiverId = :user2) OR " +
            "(m.senderId = :user2 AND m.receiverId = :user1) " +
            "ORDER BY m.sentAt ASC")
    List<Message> findConversationBetweenUsers(@Param("user1") Integer user1, @Param("user2") Integer user2);

    /**
     * Pobiera wszystkie wiadomości wysłane przez danego użytkownika, posortowane malejąco według czasu wysłania.
     *
     * @param senderId identyfikator nadawcy
     * @return lista wiadomości wysłanych przez senderId
     */
    List<Message> findBySenderIdOrderBySentAtDesc(Integer senderId);

    /**
     * Pobiera wszystkie wiadomości odebrane przez danego użytkownika, posortowane malejąco według czasu wysłania.
     *
     * @param receiverId identyfikator odbiorcy
     * @return lista wiadomości odebranych przez receiverId
     */
    List<Message> findByReceiverIdOrderBySentAtDesc(Integer receiverId);

    /**
     * Pobiera wszystkie wiadomości, w których dany użytkownik jest nadawcą lub odbiorcą,
     * posortowane malejąco według czasu wysłania.
     *
     * @param userId    identyfikator użytkownika jako nadawcy
     * @param userId1   identyfikator użytkownika jako odbiorcy (zwykle ten sam co userId)
     * @return lista wiadomości związanych z userId
     */
    List<Message> findBySenderIdOrReceiverIdOrderBySentAtDesc(Integer userId, Integer userId1);
}