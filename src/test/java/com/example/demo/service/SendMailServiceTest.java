package com.example.demo.service;

import com.example.demo.dto.MailRequest;
import com.example.demo.dto.PECMailRequest;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SendMailServiceTest {

    private JavaMailSender javaMailSender;
    private SendMailService sendMailService;

    @BeforeEach
    void setUp() {
        javaMailSender = mock(JavaMailSender.class);
        sendMailService = new SendMailService(javaMailSender);
    }

    @Test
    void testSendMail_successful() throws Exception {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        MailRequest request = new MailRequest();
        request.setFrom("test@example.com");
        request.setTo(Collections.singletonList("to@example.com"));
        request.setCc(Arrays.asList("cc1@example.com", "cc2@example.com"));
        request.setBcc(Collections.singletonList("bcc@example.com"));
        request.setReplyTo("reply@example.com");
        request.setSubject("Test Subject");
        request.setBodyText("Test Body");
        request.setSentDate(new Date());

        sendMailService.sendMail(request);

        verify(javaMailSender, times(1)).send(mimeMessage);
    }

    @Test
    void testSendMail_exceptionThrown() throws Exception {
        when(javaMailSender.createMimeMessage()).thenThrow(new RuntimeException("Creation failed"));

        MailRequest request = new MailRequest();
        request.setTo(Collections.singletonList("to@example.com"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            sendMailService.sendMail(request);
        });

        assertTrue(ex.getMessage().contains("Errore nell'invio della mail"));
    }

    @Test
    void testSendPEC_successful() throws Exception {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        PECMailRequest request = new PECMailRequest();
        request.setTo("pec@example.com");
        request.setSubject("PEC Subject");
        request.setBody("PEC Body");

        sendMailService.sendPEC(request);

        verify(javaMailSender).send(mimeMessage);
    }

    @Test
    void testSendPEC_exceptionThrown() {
        when(javaMailSender.createMimeMessage()).thenThrow(new RuntimeException("Mime creation failed"));

        PECMailRequest request = new PECMailRequest();
        request.setTo("pec@example.com");
        request.setSubject("Subject");
        request.setBody("Body");

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            sendMailService.sendPEC(request);
        });

        assertTrue(ex.getMessage().contains("Errore nell'invio della PEC"));
    }
}
