package com.example.demo.controller;

import com.example.demo.dto.PutAccommodationRequest;
import com.example.demo.service.PutAccommodationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResourceAccessException;

import java.util.UUID;

@RestController
@RequestMapping("/putAccommodation")
public class PutAccommodationController {

    private final PutAccommodationService putAccommodationService;

    private static final Logger logger = LogManager.getLogger(PutAccommodationController.class);

    public PutAccommodationController(PutAccommodationService putAccommodationService){
        this.putAccommodationService = putAccommodationService;
    }

    @PutMapping
    public ResponseEntity<String> putAccommodation(@RequestBody PutAccommodationRequest putAccommodationRequest){
        String trackingId = String.valueOf(UUID.randomUUID());
        try{
            putAccommodationService.validateWithXsd(putAccommodationRequest); // validazione dell'input
            putAccommodationService.putAccommodation(putAccommodationRequest); //richiamo metodo putAccommodation del Service
            logger.info("Richiesta di aggiornamento avvenuta con successo");

            putAccommodationService.renderJsonToCRM(putAccommodationRequest, trackingId, true); // richiamo il metodo per creazione messaggio a CRM

            // TODO: invio messaggio al CRM con SQS
            logger.info("Invio messaggio con trackingId: {}", trackingId);

            return ResponseEntity.ok("Aggiornamento avvenuto con successo");
        }catch(ResourceAccessException ex){
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body("Timeout nel contattare il servizio esterno: " + ex.getMessage());
        }catch(Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Errore interno al servizio: " + ex.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<String> postAccommodation(@RequestBody PutAccommodationRequest putAccommodationRequest){
        String trackingId = String.valueOf(UUID.randomUUID());
        try{
            putAccommodationService.validateWithXsd(putAccommodationRequest); // validazione dell'input
            putAccommodationService.postAccommodation(putAccommodationRequest); //richiamo metodo putAccommodation del Service
            logger.info("Richiesta di inserimento avvenuta con successo");

            putAccommodationService.renderJsonToCRM(putAccommodationRequest, trackingId, false); // richiamo il metodo per creazione messaggio a CRM

            // TODO: invio messaggio al CRM con SQS
            logger.info("Invio messaggio con trackingId: {}", trackingId);

            return ResponseEntity.ok("Inserimento avvenuto con successo");
        }catch(ResourceAccessException ex){
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body("Timeout nel contattare il servizio esterno: " + ex.getMessage());
        }catch(Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Errore interno al servizio: " + ex.getMessage());
        }
    }
}
