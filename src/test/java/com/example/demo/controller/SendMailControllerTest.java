package com.example.demo.controller;

import com.example.demo.dto.MailRequest;
import com.example.demo.dto.PECMailRequest;
import com.example.demo.service.SendMailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class SendMailControllerTest {

    @Mock
    private SendMailService sendMailService;

    @InjectMocks
    private SendMailController sendMailController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void contextLoads() {
        assertNotNull(sendMailController);
    }

    @Test
    void sendMail_ValidRequest_Returns200() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        // Imposta mailRequest
        MailRequest mailRequest = new MailRequest();
        mailRequest.setTo(List.of("recipient@example.com"));
        mailRequest.setSubject("Test Subject");

        doNothing().when(sendMailService).sendMail(any(MailRequest.class));

        ResponseEntity<String> response = sendMailController.sendMail(mailRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Email inviata correttamente", response.getBody());
        // Verifica che il service venga chiamato una volta sola
        verify(sendMailService, times(1)).sendMail(any(MailRequest.class));
    }

    @Test
    void sendMail_ServiceThrowsException_Returns500() {
        MailRequest mailRequest = new MailRequest();
        mailRequest.setTo(List.of("recipient@example.com"));
        mailRequest.setSubject("Test Subject");

        doThrow(new RuntimeException("Errore invio mail"))
                .when(sendMailService).sendMail(any(MailRequest.class));

        ResponseEntity<String> response = sendMailController.sendMail(mailRequest);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().contains("Errore nell'invio della mail"));
        verify(sendMailService, times(1)).sendMail(any(MailRequest.class));
    }

    @Test
    void sendPEC_ValidRequest_Returns200() {
        PECMailRequest pecRequest = new PECMailRequest();
        pecRequest.setTo("recipient@pec.example.com");
        pecRequest.setSubject("PEC Test");

        doNothing().when(sendMailService).sendPEC(any(PECMailRequest.class));

        ResponseEntity<String> response = sendMailController.sendPEC(pecRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("PEC inviata correttamente", response.getBody());
        verify(sendMailService, times(1)).sendPEC(any(PECMailRequest.class));
    }

    @Test
    void sendPEC_ServiceThrowsException_Returns500() {
        PECMailRequest pecRequest = new PECMailRequest();
        pecRequest.setTo("recipient@pec.example.com");
        pecRequest.setSubject("PEC Test");

        doThrow(new RuntimeException("Errore invio PEC"))
                .when(sendMailService).sendPEC(any(PECMailRequest.class));

        ResponseEntity<String> response = sendMailController.sendPEC(pecRequest);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().contains("Errore nell'invio della PEC"));
        verify(sendMailService, times(1)).sendPEC(any(PECMailRequest.class));
    }

}
