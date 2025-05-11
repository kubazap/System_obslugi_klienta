package pl.zapala.system_obslugi_klienta.repositories;

import pl.zapala.system_obslugi_klienta.models.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m WHERE " +
            "(m.senderId = :user1 AND m.receiverId = :user2) OR " +
            "(m.senderId = :user2 AND m.receiverId = :user1) " +
            "ORDER BY m.sentAt ASC")
    List<Message> findConversationBetweenUsers(@Param("user1") Integer user1, @Param("user2") Integer user2);

    List<Message> findBySenderIdOrderBySentAtDesc(Integer senderId);

    List<Message> findByReceiverIdOrderBySentAtDesc(Integer receiverId);

    List<Message> findBySenderIdOrReceiverIdOrderBySentAtDesc(Integer userId, Integer userId1);
}