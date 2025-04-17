package com.example.demo.service;

import com.example.sendMail.MailRequest;
import com.example.sendMail.PECMailRequest;
import com.example.sendMail.SendMailService;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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

    @Test
    void testValidateWithXsdMail_validMail_shouldPass() {
        MailRequest mailRequest = new MailRequest();
        mailRequest.setFrom("sender@example.com");
        mailRequest.setTo(List.of("recipient1@example.com", "recipient2@example.com"));
        mailRequest.setCc(List.of("cc@example.com"));
        mailRequest.setBcc(List.of("bcc@example.com"));
        mailRequest.setReplyTo("reply@example.com");
        mailRequest.setSubject("Test Subject");
        mailRequest.setSentDate(new Date());
        mailRequest.setBodyText("This is a test email body.");
        mailRequest.setContentType("text/plain");

        assertDoesNotThrow(() -> sendMailService.validateWithXsdMail(mailRequest));
    }

    @Test
    public void validateWithXsdPECMail_shouldPassValidation() {
        PECMailRequest request = new PECMailRequest();
        request.setTo("destinatario@pec.it");
        request.setSubject("Test invio PEC");
        request.setBody("Contenuto di test per il corpo della mail PEC.");
        request.setFullName("Mario Rossi");

        assertDoesNotThrow(() -> sendMailService.validateWithXsdPECMail(request));
    }
}
