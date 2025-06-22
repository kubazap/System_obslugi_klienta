package pl.zapala.system_obslugi_klienta;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import pl.zapala.system_obslugi_klienta.controllers.MessageController;
import pl.zapala.system_obslugi_klienta.models.Message;
import pl.zapala.system_obslugi_klienta.models.MessageDto;
import pl.zapala.system_obslugi_klienta.models.Pracownik;
import pl.zapala.system_obslugi_klienta.models.PracownikDto;
import pl.zapala.system_obslugi_klienta.repositories.MessageRepository;
import pl.zapala.system_obslugi_klienta.repositories.PracownikRepository;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.time.Instant;
import java.util.Map;

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
class MessageControllerTest {

    @Autowired
    private MessageController messageController;

    @Autowired
    private PracownikRepository pracownikRepository;
    @Autowired
    private MessageRepository messageRepository;

    private MessageDto messageDto;
    private MessageDto messageDto2;
    private Pracownik pracownik;
    private Pracownik pracownik2;

    @BeforeEach
    void setUp() {
        messageRepository.deleteAll();
        pracownikRepository.deleteAll();

        pracownik = new Pracownik();
        pracownik.setImie("Jan");
        pracownik.setNazwisko("Kowalski");
        pracownik2 = new Pracownik();
        pracownik2.setImie("Janek");
        pracownik2.setNazwisko("Nowak");
        pracownikRepository.save(pracownik);
        pracownikRepository.save(pracownik2);

        messageDto = new MessageDto();
        messageDto.setId(1L);
        messageDto.setSenderId(pracownik.getId());
        messageDto.setSenderFirstName(pracownik.getImie());
        messageDto.setSenderLastName(pracownik.getNazwisko());
        messageDto.setReceiverId(pracownik2.getId());
        messageDto.setReceiverFirstName(pracownik2.getImie());
        messageDto.setReceiverLastName(pracownik2.getNazwisko());
        messageDto.setContent("Hello");
        messageDto.setTimestamp("1");

        messageDto2 = new MessageDto();
        messageDto2.setId(2L);
        messageDto2.setSenderId(pracownik2.getId());
        messageDto2.setReceiverFirstName(pracownik2.getImie());
        messageDto2.setReceiverLastName(pracownik2.getNazwisko());
        messageDto2.setReceiverId(pracownik.getId());
        messageDto2.setSenderFirstName(pracownik.getImie());
        messageDto2.setSenderLastName(pracownik.getNazwisko());
        messageDto2.setContent("Hello2");
        messageDto.setTimestamp("2");
    }

    @Nested
    @DisplayName("Operacje na wiadomościach")
    class MessageControllerTests {

        @Test
        @DisplayName("Pobieranie wiadomości między pracownikami")
        void ShouldList() {

            messageController.sendMessage(messageDto);
            messageController.sendMessage(messageDto2);
            messageDto.getId();
            List<MessageDto> messages = messageController.getConversation(pracownik.getId(),pracownik2.getId());
            assertEquals(2, messages.size());
            assertEquals(messages.get(0).getSenderFirstName(),"Jan");
            assertEquals(messages.get(0).getContent(),"Hello");
            assertEquals(messages.get(0).getReceiverLastName(),"Nowak");
            assertEquals(messages.get(0).getReceiverFirstName(),"Janek");
            assertEquals(messages.get(0).getSenderLastName(),messageDto.getSenderLastName());
            String timestamp = messages.get(0).getTimestamp().substring(0, 10);  // np. "2025-06-21T13:34:48.739711Z"
            String time1 = Instant.now().toString().substring(0, 10);
            assertEquals(timestamp, time1);
        }

        @Test
        @DisplayName("Wysłanie wiadomości")
        void sendSuccessfully() {
            Message message = messageController.sendMessage(messageDto);
            message.getId();
            assertEquals(message.getSenderFirstName(),"Jan");
            assertEquals(message.getSenderLastName(),"Kowalski");
            assertEquals(message.getReceiverLastName(),"Nowak");
            assertEquals(message.getReceiverFirstName(),"Janek");
            assertEquals(message.getReceiverId(),messageDto.getReceiverId());

        }

        @Test
        @DisplayName("Lista wiadomości z każdym pracownikiem")
        void ShouldList2() {
            messageController.sendMessage(messageDto);
            messageController.sendMessage(messageDto2);
            ResponseEntity<List<Map<String, Object>>> response = messageController.getConversations(pracownik.getId());
            List<Map<String, Object>> conversations = response.getBody();
            assertNotNull(response);
            assertFalse(conversations.isEmpty());
        }

        @Test
        @DisplayName("Pobranie pracowików")
        void GetPracownicy() {

            messageController.sendMessage(messageDto);
            ResponseEntity<List<PracownikDto>> response = messageController.getAllPracownicy();
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());

            List<PracownikDto> pracownicy = response.getBody();
            assertNotNull(pracownicy);
            assertEquals(2, pracownicy.size());
            assertEquals("Jan", pracownicy.get(0).getImie());
            assertEquals("Kowalski", pracownicy.get(0).getNazwisko());

        }
    }
}