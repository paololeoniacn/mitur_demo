package com.example.putAtecoNew;

import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResourceAccessException;

@RestController
@RequestMapping("/putAtecoNew")
public class PutAtecoNewController {

    private final PutAtecoNewService putAtecoNewService;

    private static final Logger logger = LogManager.getLogger(PutAtecoNewController.class);

    public PutAtecoNewController(PutAtecoNewService putAtecoNewService){
        this.putAtecoNewService = putAtecoNewService;
    }

    @PutMapping
    public ResponseEntity<String> putAtecoNew(@RequestBody @Valid PutAtecoNewRequest putAtecoNewRequest){
        try{
            putAtecoNewService.putAtecoNew(putAtecoNewRequest, true);
            logger.info("Richiesta di aggiornamento avvenuta con successo");

            // TODO: salvare request su DB

            return ResponseEntity.ok("Aggiornamento avvenuto con successo");
        }catch(ResourceAccessException ex){
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body("Timeout nel contattare il servizio esterno: " + ex.getMessage());
        }catch(Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Errore interno al servizio: " + ex.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<String> postAtecoNew(@RequestBody @Valid PutAtecoNewRequest putAtecoNewRequest){
        try{
            putAtecoNewService.putAtecoNew(putAtecoNewRequest, false);
            logger.info("Richiesta di inserimento avvenuta con successo");

            // TODO: salvare request su DB

            return ResponseEntity.ok("Inserimento avvenuto con successo");
        }catch(ResourceAccessException ex){
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body("Timeout nel contattare il servizio esterno: " + ex.getMessage());
        }catch(Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Errore interno al servizio: " + ex.getMessage());
        }
    }
}
