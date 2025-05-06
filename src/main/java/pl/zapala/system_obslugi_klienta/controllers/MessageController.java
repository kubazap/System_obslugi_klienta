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
            @PathVariable Integer userId
    ) {
        var sent     = messageRepository.findBySenderIdOrderBySentAtDesc(userId);
        var received = messageRepository.findByReceiverIdOrderBySentAtDesc(userId);

        Map<Integer, Message> lastByPartner = new HashMap<>();

        Consumer<Message> accumulate = msg -> {
            Integer partnerId = Objects.equals(msg.getSenderId(), userId)
                    ? msg.getReceiverId()
                    : msg.getSenderId();
            lastByPartner.compute(partnerId, (key, existing) -> {
                if (existing == null || msg.getSentAt().isAfter(existing.getSentAt())) {
                    return msg;
                }
                return existing;
            });
        };

        sent.forEach(accumulate);
        received.forEach(accumulate);

        List<Map<String, Object>> conversations = lastByPartner.entrySet().stream()
                .flatMap(entry -> pracownikRepository.findById(entry.getKey()).stream()
                        .map(pracownik -> {
                            Map<String, Object> info = new HashMap<>();
                            info.put("rozmowcaId", entry.getKey());
                            info.put("rozmowcaImie", pracownik.getImie());
                            info.put("rozmowcaNazwisko", pracownik.getNazwisko());
                            info.put("ostatniaWiadomoscTresc", entry.getValue().getContent());
                            return info;
                        }))
                .toList();

        return ResponseEntity.ok(conversations);
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
