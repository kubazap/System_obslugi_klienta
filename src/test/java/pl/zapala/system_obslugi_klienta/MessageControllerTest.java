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

import pl.zapala.system_obslugi_klienta.models.Pracownik;
import pl.zapala.system_obslugi_klienta.models.PracownikDto;
import pl.zapala.system_obslugi_klienta.repositories.MessageRepository;
import pl.zapala.system_obslugi_klienta.repositories.PracownikRepository;


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

    private MessageDto messageDto;

    @BeforeEach
    void setUp() {

    }

    @Nested
    @DisplayName("Operacje na wiadomościach")
    class DokumentTests {

        @Test
        @DisplayName("Lista wiadomości")
        void ShouldList() {
            Pracownik pracownik = new Pracownik();
            Pracownik pracownik2 = new Pracownik();

            pracownik.setImie("Jan");
            pracownik.setNazwisko("Kowalski");

            pracownik2.setImie("Janek");
            pracownik2.setNazwisko("Nowak");
            pracownikRepository.save(pracownik);
            pracownikRepository.save(pracownik2);

            messageDto = new MessageDto();
            messageDto.setId(1L);
            messageDto.setSenderId(pracownik.getId());
            messageDto.setSenderFirstName("Kacper");
            messageDto.setSenderLastName("Wojtyra");
            messageDto.setReceiverId(pracownik2.getId());
            messageDto.setReceiverFirstName("Jakub");
            messageDto.setReceiverLastName("Zapala");
            messageDto.setContent("Hello");

            messageDto = new MessageDto();
            messageDto.setId(2L);
            messageDto.setSenderId(pracownik2.getId());
            messageDto.setReceiverFirstName("Jakub");
            messageDto.setReceiverLastName("Zapala");
            messageDto.setReceiverId(pracownik.getId());
            messageDto.setSenderFirstName("Kacper");
            messageDto.setSenderLastName("Wojtyra");
            messageDto.setContent("Hello");

            messageController.sendMessage(messageDto);

            PracownikDto sender = new PracownikDto();
            sender.setId(pracownik.getId());
            sender.setImie("Jan");
            sender.setNazwisko("Kowalski");
            PracownikDto receiver = new PracownikDto();
            receiver.setId(pracownik.getId());
            receiver.setImie("Anna");
            receiver.setNazwisko("Nowak");
            messageController.getConversation(sender.getId(),receiver.getId());

        }

        @Test
        @DisplayName("Dodawanie dokumentu bez pliku")
        void sendSuccessfully() {
            Pracownik pracownik = new Pracownik();
            Pracownik pracownik2 = new Pracownik();

            pracownik.setImie("Jan");
            pracownik.setNazwisko("Kowalski");

            pracownik2.setImie("Janek");
            pracownik2.setNazwisko("Nowak");
            pracownikRepository.save(pracownik);
            pracownikRepository.save(pracownik2);

            messageDto = new MessageDto();
            messageDto.setId(1L);
            messageDto.setSenderId(pracownik.getId());
            messageDto.setSenderFirstName("Kacper");
            messageDto.setSenderLastName("Wojtyra");
            messageDto.setReceiverId(pracownik2.getId());
            messageDto.setReceiverFirstName("Jakub");
            messageDto.setReceiverLastName("Zapala");
            messageDto.setContent("Hello");

            messageDto = new MessageDto();
            messageDto.setId(2L);
            messageDto.setSenderId(pracownik2.getId());
            messageDto.setReceiverFirstName("Jakub");
            messageDto.setReceiverLastName("Zapala");
            messageDto.setReceiverId(pracownik.getId());
            messageDto.setSenderFirstName("Kacper");
            messageDto.setSenderLastName("Wojtyra");
            messageDto.setContent("Hello");

            messageController.sendMessage(messageDto);

        }

        @Test
        @DisplayName("Lista wiadomości")
        void ShouldList2() {
            Pracownik pracownik = new Pracownik();
            Pracownik pracownik2 = new Pracownik();

            pracownik.setImie("Jan");
            pracownik.setNazwisko("Kowalski");

            pracownik2.setImie("Janek");
            pracownik2.setNazwisko("Nowak");
            pracownikRepository.save(pracownik);
            pracownikRepository.save(pracownik2);

            messageDto = new MessageDto();
            messageDto.setId(1L);
            messageDto.setSenderId(pracownik.getId());
            messageDto.setSenderFirstName("Kacper");
            messageDto.setSenderLastName("Wojtyra");
            messageDto.setReceiverId(pracownik2.getId());
            messageDto.setReceiverFirstName("Jakub");
            messageDto.setReceiverLastName("Zapala");
            messageDto.setContent("Hello");

            messageDto = new MessageDto();
            messageDto.setId(2L);
            messageDto.setSenderId(pracownik2.getId());
            messageDto.setReceiverFirstName("Jakub");
            messageDto.setReceiverLastName("Zapala");
            messageDto.setReceiverId(pracownik.getId());
            messageDto.setSenderFirstName("Kacper");
            messageDto.setSenderLastName("Wojtyra");
            messageDto.setContent("Hello");

            messageController.sendMessage(messageDto);
            PracownikDto sender = new PracownikDto();
            sender.setId(pracownik.getId());
            sender.setImie("Jan");
            sender.setNazwisko("Kowalski");
            messageController.getConversations(sender.getId());

        }

        @Test
        @DisplayName("Dodawanie dokumentu bez pliku")
        void GetPracownicy() {
            Pracownik pracownik = new Pracownik();
            Pracownik pracownik2 = new Pracownik();

            pracownik.setImie("Jan");
            pracownik.setNazwisko("Kowalski");

            pracownik2.setImie("Janek");
            pracownik2.setNazwisko("Nowak");
            pracownikRepository.save(pracownik);
            pracownikRepository.save(pracownik2);

            messageDto = new MessageDto();
            messageDto.setId(1L);
            messageDto.setSenderId(pracownik.getId());
            messageDto.setSenderFirstName("Kacper");
            messageDto.setSenderLastName("Wojtyra");
            messageDto.setReceiverId(pracownik2.getId());
            messageDto.setReceiverFirstName("Jakub");
            messageDto.setReceiverLastName("Zapala");
            messageDto.setContent("Hello");

            messageDto = new MessageDto();
            messageDto.setId(2L);
            messageDto.setSenderId(pracownik2.getId());
            messageDto.setReceiverFirstName("Jakub");
            messageDto.setReceiverLastName("Zapala");
            messageDto.setReceiverId(pracownik.getId());
            messageDto.setSenderFirstName("Kacper");
            messageDto.setSenderLastName("Wojtyra");
            messageDto.setContent("Hello");

            messageController.sendMessage(messageDto);

            messageController.getAllPracownicy();

        }

    }
}