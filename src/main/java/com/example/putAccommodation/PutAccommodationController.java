package com.example.putAccommodation;

import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResourceAccessException;


@RestController
@RequestMapping("/putAccommodation")
public class PutAccommodationController {

    private final PutAccommodationService putAccommodationService;

    private static final Logger logger = LogManager.getLogger(PutAccommodationController.class);

    public PutAccommodationController(PutAccommodationService putAccommodationService){
        this.putAccommodationService = putAccommodationService;
    }

    @PutMapping
    public ResponseEntity<String> putAccommodation(@RequestBody @Valid PutAccommodationRequest putAccommodationRequest){
        try{
            putAccommodationService.putAccommodation(putAccommodationRequest, true); //richiamo metodo putAccommodation del Service
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
    public ResponseEntity<String> postAccommodation(@RequestBody @Valid PutAccommodationRequest putAccommodationRequest){
        try{
            putAccommodationService.putAccommodation(putAccommodationRequest, false); //richiamo metodo postAccommodation del Service
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
