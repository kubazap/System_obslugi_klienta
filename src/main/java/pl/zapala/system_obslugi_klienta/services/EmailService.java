package pl.zapala.system_obslugi_klienta.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.context.Context;
import pl.zapala.system_obslugi_klienta.exception.EmailSendingException;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;

    public EmailService(JavaMailSender javaMailSender,
                        SpringTemplateEngine templateEngine) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
    }

    @Value("${emails.sender_email}")
    private String senderEmail;
    @Value("${emails.sender_name}")
    private String senderName;

    @Async
    public void sendTotpEmail(String imie, String receiverEmail, String code) {
        try {
            MimeMessage mime = javaMailSender.createMimeMessage();
            MimeMessageHelper msg = new MimeMessageHelper(mime, true, "UTF-8");

            msg.setSubject("Kod weryfikacyjny");
            msg.setTo(receiverEmail);
            msg.setFrom(senderEmail, senderName);

            Context ctx = new Context();
            ctx.setVariable("pracownikImie", imie);
            ctx.setVariable("codeEmail", code);

            String html = templateEngine.process("logowanie/e-mail", ctx);
            msg.setText(html, true);

            javaMailSender.send(mime);
            logger.info("Confirmation email sent to {}", receiverEmail);
        } catch (MessagingException e) {
            throw new EmailSendingException(
                    "Błąd tworzenia MIME dla maila do " + receiverEmail, e);
        } catch (Exception e) {
            throw new EmailSendingException(
                    "Nie udało się wysłać maila do " + receiverEmail, e);
        }
    }
}

