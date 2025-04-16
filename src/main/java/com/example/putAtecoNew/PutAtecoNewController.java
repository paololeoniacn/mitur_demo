package com.example.putAtecoNew;

import com.example.putAccommodation.PutAccommodationRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResourceAccessException;

import java.util.UUID;

@RestController
@RequestMapping("/putAtecoNew")
public class PutAtecoNewController {

    private final PutAtecoNewService putAtecoNewService;

    private static final Logger logger = LogManager.getLogger(PutAtecoNewController.class);

    public PutAtecoNewController(PutAtecoNewService putAtecoNewService){
        this.putAtecoNewService = putAtecoNewService;
    }

    @PostMapping
    public ResponseEntity<String> postAtecoNew(@RequestBody PutAtecoNewRequest putAtecoNewRequest){
        String trackingId = String.valueOf(UUID.randomUUID());
        try{
            putAtecoNewService.validateWithXsd(putAtecoNewRequest); // validazione dell'input
            putAtecoNewService.postAtecoNew(putAtecoNewRequest); //richiamo metodo putAccommodation del Service
            logger.info("Richiesta di inserimento avvenuta con successo");

            // TODO: capire se inviare coda o salvare su DB

            return ResponseEntity.ok("Inserimento avvenuto con successo");
        }catch(ResourceAccessException ex){
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body("Timeout nel contattare il servizio esterno: " + ex.getMessage());
        }catch(Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Errore interno al servizio: " + ex.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<String> putAtecoNew(@RequestBody PutAtecoNewRequest putAtecoNewRequest){
        String trackingId = String.valueOf(UUID.randomUUID());
        try{
            putAtecoNewService.validateWithXsd(putAtecoNewRequest); // validazione dell'input
            putAtecoNewService.putAtecoNew(putAtecoNewRequest); //richiamo metodo putAccommodation del Service
            logger.info("Richiesta di aggiornamento avvenuta con successo");

            // TODO: capire se inviare coda o salvare su DB
            //putAccommodationService.renderJsonToCRM(putAccommodationRequest, trackingId, false); // richiamo il metodo per creazione messaggio a CRM
            // logger.info("Invio messaggio con trackingId: {}", trackingId);

            return ResponseEntity.ok("Aggiornamento avvenuto con successo");
        }catch(ResourceAccessException ex){
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body("Timeout nel contattare il servizio esterno: " + ex.getMessage());
        }catch(Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Errore interno al servizio: " + ex.getMessage());
        }
    }


}
