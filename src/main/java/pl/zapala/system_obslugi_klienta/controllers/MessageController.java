package pl.zapala.system_obslugi_klienta.controllers;

import pl.zapala.system_obslugi_klienta.dto.MessageDTO;
import pl.zapala.system_obslugi_klienta.dto.PracownikDTO;
import pl.zapala.system_obslugi_klienta.models.Message;
import pl.zapala.system_obslugi_klienta.models.Pracownik;
import pl.zapala.system_obslugi_klienta.repositories.MessageRepository;
import pl.zapala.system_obslugi_klienta.repositories.PracownikRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.function.Consumer;

@RestController
@RequestMapping("/messages")
@CrossOrigin(origins = "*")
public class MessageController {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private PracownikRepository pracownikRepository;

    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    @GetMapping("/{user1Id}/{user2Id}")
    public List<MessageDTO> getConversation(@PathVariable Integer user1Id, @PathVariable Integer user2Id) {
        List<Message> messages = messageRepository.findConversationBetweenUsers(user1Id, user2Id);
        return messages.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private MessageDTO convertToDTO(Message message) {
        MessageDTO dto = new MessageDTO();
        dto.setId(message.getId());
        dto.setSenderId(message.getSenderId());
        dto.setReceiverId(message.getReceiverId());
        dto.setContent(message.getContent());
        if (message.getSentAt() != null) {
            dto.setTimestamp(message.getSentAt().format(formatter));
        }
        setPracownikData(message.getSenderId(), pracownik -> {
            dto.setSenderFirstName(pracownik.getImie());
            dto.setSenderLastName(pracownik.getNazwisko());
        });
        setPracownikData(message.getReceiverId(), pracownik -> {
            dto.setReceiverFirstName(pracownik.getImie());
            dto.setReceiverLastName(pracownik.getNazwisko());
        });
        return dto;
    }

    @PostMapping
    public Message sendMessage(@RequestBody MessageDTO messageDTO) {
        Message message = new Message();
        message.setSenderId(messageDTO.getSenderId());
        message.setReceiverId(messageDTO.getReceiverId());
        setPracownikData(messageDTO.getSenderId(), pracownik -> {
            message.setSenderFirstName(pracownik.getImie());
            message.setSenderLastName(pracownik.getNazwisko());
        });
        setPracownikData(messageDTO.getReceiverId(), pracownik -> {
            message.setReceiverFirstName(pracownik.getImie());
            message.setReceiverLastName(pracownik.getNazwisko());
        });
        message.setContent(messageDTO.getContent());
        message.setSentAt(OffsetDateTime.now(ZoneId.of("Europe/Warsaw")));
        return messageRepository.save(message);
    }

    @GetMapping("/conversations/{userId}")
    public ResponseEntity<List<Map<String, Object>>> getConversations(@PathVariable Integer userId) {
        List<Message> sentMessages = messageRepository.findBySenderIdOrderBySentAtDesc(userId);
        List<Message> receivedMessages = messageRepository.findByReceiverIdOrderBySentAtDesc(userId);

        Map<Integer, Message> lastMessagesWithRozmowca = new HashMap<>();

        // Przetwarzanie wysłanych wiadomości
        for (Message message : sentMessages) {
            Integer rozmowcaId = message.getReceiverId();
            if (!lastMessagesWithRozmowca.containsKey(rozmowcaId) || message.getSentAt().isAfter(lastMessagesWithRozmowca.get(rozmowcaId).getSentAt())) {
                lastMessagesWithRozmowca.put(rozmowcaId, message);
            }
        }

        // Przetwarzanie odebranych wiadomości
        for (Message message : receivedMessages) {
            Integer rozmowcaId = message.getSenderId();
            if (!lastMessagesWithRozmowca.containsKey(rozmowcaId) || message.getSentAt().isAfter(lastMessagesWithRozmowca.get(rozmowcaId).getSentAt())) {
                lastMessagesWithRozmowca.put(rozmowcaId, message);
            }
        }

        List<Map<String, Object>> conversations = new ArrayList<>();
        for (Map.Entry<Integer, Message> entry : lastMessagesWithRozmowca.entrySet()) {
            Integer rozmowcaId = entry.getKey();
            Message lastMessage = entry.getValue();

            Pracownik rozmowca = pracownikRepository.findById(rozmowcaId).orElse(null);
            if (rozmowca != null) {
                Map<String, Object> conversationInfo = new HashMap<>();
                conversationInfo.put("rozmowcaId", rozmowcaId);
                conversationInfo.put("rozmowcaImie", rozmowca.getImie());
                conversationInfo.put("rozmowcaNazwisko", rozmowca.getNazwisko());
                conversationInfo.put("ostatniaWiadomoscTresc", lastMessage.getContent());
                conversations.add(conversationInfo);
            }
        }

        return ResponseEntity.ok(conversations);
    }

    @GetMapping("/users")
    public ResponseEntity<List<PracownikDTO>> getAllPracownicy() {
        List<Pracownik> pracownicy = pracownikRepository.findAll();
        List<PracownikDTO> pracownikDTOs = pracownicy.stream()
                .map(this::convertToPracownikDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(pracownikDTOs);
    }

    private void setPracownikData(Integer pracownikId, Consumer<Pracownik> setter) {
        pracownikRepository.findById(pracownikId)
                .ifPresent(setter);
    }

    private PracownikDTO convertToPracownikDTO(Pracownik pracownik) {
        PracownikDTO dto = new PracownikDTO();
        dto.setId(pracownik.getId());
        dto.setImie(pracownik.getImie());
        dto.setNazwisko(pracownik.getNazwisko());
        return dto;
    }
}