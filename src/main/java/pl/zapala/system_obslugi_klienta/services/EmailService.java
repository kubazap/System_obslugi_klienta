package pl.zapala.system_obslugi_klienta.services;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private SpringTemplateEngine templateEngine;

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
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

