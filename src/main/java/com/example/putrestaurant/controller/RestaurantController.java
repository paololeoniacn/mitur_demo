package com.example.putrestaurant.controller;

import com.example.putrestaurant.dto.RestaurantDTO;
import com.example.putrestaurant.service.RestaurantService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/restaurant")
public class RestaurantController {

    private static final Logger logger = LoggerFactory.getLogger(RestaurantController.class);

    private final RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @PostMapping
    public ResponseEntity<String> createRestaurant(@Valid @RequestBody RestaurantDTO dto) {
        logger.info("Richiesta POST ricevuta per la creazione del tour operator: {}", dto);
        try {
            restaurantService.processRestaurant(dto, false);
            logger.info("Tour operator creato con successo.");
            return ResponseEntity.ok("Tour operator creato con successo.");
        } catch (Exception e) {
            logger.error("Errore durante la creazione del tour operator", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore durante la creazione del tour operator.");
        }
    }

    @PutMapping
    public ResponseEntity<String> updateRestaurant(@Valid @RequestBody RestaurantDTO dto) {
        logger.info("Richiesta PUT ricevuta per l'aggiornamento del tour operator: {}", dto);
        try {
            restaurantService.processRestaurant(dto, true);
            logger.info("Tour operator aggiornato con successo.");
            return ResponseEntity.ok("Tour operator aggiornato con successo.");
        } catch (Exception e) {
            logger.error("Errore durante l'aggiornamento del tour operator", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore durante l'aggiornamento del tour operator.");
        }
    }
}

