package pl.zapala.system_obslugi_klienta.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.zapala.system_obslugi_klienta.models.Message;
import pl.zapala.system_obslugi_klienta.models.MessageDto;
import pl.zapala.system_obslugi_klienta.models.Pracownik;
import pl.zapala.system_obslugi_klienta.models.PracownikDto;
import pl.zapala.system_obslugi_klienta.repositories.MessageRepository;
import pl.zapala.system_obslugi_klienta.repositories.PracownikRepository;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;

@RestController
@RequestMapping("/messages")
@CrossOrigin(origins = "*")
public class MessageController {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    private static final ZoneId WARSAW_ZONE = ZoneId.of("Europe/Warsaw");

    private final MessageRepository messageRepository;
    private final PracownikRepository pracownikRepository;

    public MessageController(
            MessageRepository messageRepository,
            PracownikRepository pracownikRepository) {
        this.messageRepository = messageRepository;
        this.pracownikRepository = pracownikRepository;
    }

    @GetMapping("/{user1Id}/{user2Id}")
    public List<MessageDto> getConversation(
            @PathVariable Integer user1Id,
            @PathVariable Integer user2Id
    ) {
        return messageRepository
                .findConversationBetweenUsers(user1Id, user2Id)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    @PostMapping
    public Message sendMessage(@RequestBody MessageDto messageDto) {
        Message message = new Message();
        message.setSenderId(messageDto.getSenderId());
        message.setReceiverId(messageDto.getReceiverId());

        setPracownikData(messageDto.getSenderId(), pracownik -> {
            message.setSenderFirstName(pracownik.getImie());
            message.setSenderLastName(pracownik.getNazwisko());
        });
        setPracownikData(messageDto.getReceiverId(), pracownik -> {
            message.setReceiverFirstName(pracownik.getImie());
            message.setReceiverLastName(pracownik.getNazwisko());
        });

        message.setContent(messageDto.getContent());
        message.setSentAt(OffsetDateTime.now(WARSAW_ZONE));
        return messageRepository.save(message);
    }

    @GetMapping("/conversations/{userId}")
    public ResponseEntity<List<Map<String, Object>>> getConversations(
            @PathVariable Integer userId) {

    /* 1.  Pobierz WSZYSTKIE wiadomości, w których użytkownik bierze udział
           (są już posortowane malejąco po dacie) */
        List<Message> all = messageRepository
                .findBySenderIdOrReceiverIdOrderBySentAtDesc(userId, userId);

        /* 2.  Dla każdego rozmówcy zapamiętaj NAJNOWSZĄ wiadomość */
        Map<Integer, Message> lastMsg = new HashMap<>();
        for (Message m : all) {
            int partner = Objects.equals(m.getSenderId(), userId)
                    ? m.getReceiverId()
                    : m.getSenderId();

            lastMsg.compute(partner,
                    (k, old) -> old == null || m.getSentAt().isAfter(old.getSentAt()) ? m : old);
        }

        /* 3.  Zbuduj listę DTO dla KAŻDEGO pracownika ≠ userId */
        List<Map<String, Object>> list = pracownikRepository.findAll().stream()
                .filter(p -> !p.getId().equals(userId))                // pomiń siebie
                .map(p -> {
                    Message m = lastMsg.get(p.getId());                // może być null
                    Map<String, Object> dto = new HashMap<>();
                    dto.put("rozmowcaId",       p.getId());
                    dto.put("rozmowcaImie",     p.getImie());
                    dto.put("rozmowcaNazwisko", p.getNazwisko());
                    dto.put("ostatniaWiadomoscTresc", m != null ? m.getContent()   : null);
                    dto.put("ostatniaWiadomoscCzas",  m != null ? m.getSentAt()    : null);
                    return dto;
                })
                /* 4.  Sortuj:
                       – najpierw ci z wiadomościami (malejąco po dacie),
                       – potem bez wiadomości – alfabetycznie „Nazwisko ► Imię” */
                .sorted((a,b) -> {
                    OffsetDateTime ta = (OffsetDateTime) a.get("ostatniaWiadomoscCzas");
                    OffsetDateTime tb = (OffsetDateTime) b.get("ostatniaWiadomoscCzas");

                    if (ta != null && tb != null) return tb.compareTo(ta);   // obie mają
                    if (ta != null) return -1;                               // a ma, b nie
                    if (tb != null) return  1;                               // b ma, a nie

                    int ln = ((String) a.get("rozmowcaNazwisko"))
                            .compareToIgnoreCase((String) b.get("rozmowcaNazwisko"));
                    return ln != 0 ? ln :
                            ((String) a.get("rozmowcaImie"))
                                    .compareToIgnoreCase((String) b.get("rozmowcaImie"));
                })
                .toList();

        return ResponseEntity.ok(list);
    }

    @GetMapping("/users")
    public ResponseEntity<List<PracownikDto>> getAllPracownicy() {
        List<PracownikDto> dtos = pracownikRepository.findAll().stream()
                .map(this::convertToPracownikDTO)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    private void setPracownikData(Integer pracownikId, Consumer<Pracownik> setter) {
        pracownikRepository.findById(pracownikId)
                .ifPresent(setter);
    }

    private MessageDto convertToDTO(Message message) {
        var dto = new MessageDto();
        dto.setId(message.getId());
        dto.setSenderId(message.getSenderId());
        dto.setReceiverId(message.getReceiverId());
        dto.setContent(message.getContent());
        if (message.getSentAt() != null) {
            dto.setTimestamp(message.getSentAt().format(FORMATTER));
        }
        setPracownikData(message.getSenderId(), p -> {
            dto.setSenderFirstName(p.getImie());
            dto.setSenderLastName(p.getNazwisko());
        });
        setPracownikData(message.getReceiverId(), p -> {
            dto.setReceiverFirstName(p.getImie());
            dto.setReceiverLastName(p.getNazwisko());
        });
        return dto;
    }

    private PracownikDto convertToPracownikDTO(Pracownik p) {
        var dto = new PracownikDto();
        dto.setId(p.getId());
        dto.setImie(p.getImie());
        dto.setNazwisko(p.getNazwisko());
        return dto;
    }
}
