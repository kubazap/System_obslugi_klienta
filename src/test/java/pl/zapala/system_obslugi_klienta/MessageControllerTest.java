package pl.zapala.system_obslugi_klienta;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import pl.zapala.system_obslugi_klienta.controllers.MessageController;
import pl.zapala.system_obslugi_klienta.models.MessageDto;
import pl.zapala.system_obslugi_klienta.models.Message;
import pl.zapala.system_obslugi_klienta.models.PracownikDto;
import pl.zapala.system_obslugi_klienta.repositories.MessageRepository;

import java.sql.Date;
import java.util.ArrayList;
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
class MessageControllerTest {

    @Autowired
    private MessageController messageController;

    @Autowired
    private MessageRepository messageRepository;

    private MessageDto messageDto;

    @BeforeEach
    void setUp() {
        messageRepository.deleteAll();

        messageDto = new MessageDto();
        messageDto.setId(1L);
        messageDto.setSenderId(1);
        messageDto.setSenderFirstName("Kacper");
        messageDto.setSenderLastName("Wojtyra");
        messageDto.setReceiverId(2);
        messageDto.setReceiverFirstName("Jakub");
        messageDto.setReceiverLastName("Zapala");
        messageDto.setContent("Hello");
    }

    @Nested
    @DisplayName("Operacje na dokumentach")
    class DokumentTests {

        @Test
        @DisplayName("Dodawanie dokumentu bez pliku")
        void sendSuccessfully() {
            Message message= new Message();
            message.setSenderId(1);
            message.setSenderFirstName("Kacper");
            message.setSenderLastName("Wojtyra");
            message.setReceiverId(2);
            message.setReceiverFirstName("Jakub");
            message.setReceiverLastName("Zapala");
            message.setContent("Hello");
            PracownikDto sender = new PracownikDto(1, "Jan", "Kowalski");
            PracownikDto receiver = new PracownikDto(2, "Anna", "Nowak");
            messageController.sendMessage(messageDto);

        }

    }
}