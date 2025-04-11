package com.example.demo.controller;

import com.example.demo.dto.MailRequest;
import com.example.demo.dto.PECMailRequest;
import com.example.demo.service.SendMailService;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.SSLContext;
import java.security.KeyStore;

@RestController
@RequestMapping("/api")
public class SendMailController {

    private SendMailService sendMailService;

    private static final Logger logger = LogManager.getLogger(SendMailController.class);

    @Autowired
    public SendMailController(SendMailService sendMailService){
        this.sendMailService = sendMailService;
    }

    @PostMapping("/mail")
    public ResponseEntity<String> sendMail(@Valid @RequestBody MailRequest mailRequest){
        logger.info("Richiesta di invio mail ricevuta");
        try{
            sendMailService.sendMail(mailRequest);
            return ResponseEntity.ok("Email inviata correttamente");
        } catch(Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore nell'invio della mail: " + e.getMessage());
        }

    }

    @PostMapping("/pec")
    public ResponseEntity<String> sendPEC(@Valid @RequestBody PECMailRequest pecMailRequest){
        logger.info("Richiesta di invio PEC ricevuta");
        try{
            sendMailService.sendPEC(pecMailRequest);
            return ResponseEntity.ok("PEC inviata correttamente");
        } catch(Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore nell'invio della PEC: " + e.getMessage());
        }
    }

}
