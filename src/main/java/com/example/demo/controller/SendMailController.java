package com.example.demo.controller;

import com.example.demo.dto.MailRequest;
import com.example.demo.dto.PECMailRequest;
import com.example.demo.service.SendMailService;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class SendMailController {

    private SendMailService sendMailService;

    private static final Logger logger = LogManager.getLogger(SendMailController.class);

    @PostConstruct
    public void init() {
        // Carica il file jks dal classpath
        try {
            // todo: aggiungere file jks a resources e indicare il path e la password anche in application.properties
            ClassPathResource resource = new ClassPathResource("your-keystore.jks");
            KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            keystore.load(resource.getInputStream(), "your_jks_password".toCharArray());
            logger.info("JKS inizializzato correttamente");

            // Configura il SSL context con il jks caricato
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, null, null);
        } catch (Exception e) {
            logger.info("Errore inizializzazione JKS: " + e.getMessage());
            throw new RuntimeException("Errore: JKS non inizializzato correttamente", e);
        }
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
            sendMailService.sendPec(pecMailRequest);
            return ResponseEntity.ok("Email inviata correttamente");
        } catch(Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore nell'invio della mail: " + e.getMessage());
        }
    }

}
