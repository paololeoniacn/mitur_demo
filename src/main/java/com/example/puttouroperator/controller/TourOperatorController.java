package com.example.puttouroperator.controller;


import com.example.puttouroperator.dto.TourOperatorDTO;
import com.example.puttouroperator.service.TourOperatorService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/tour-operator")
public class TourOperatorController {

    private static final Logger logger = LoggerFactory.getLogger(TourOperatorController.class);

    private final TourOperatorService tourOperatorService;

    public TourOperatorController(TourOperatorService tourOperatorService) {
        this.tourOperatorService = tourOperatorService;
    }

    @PostMapping
    public ResponseEntity<String> createTourOperator(@Valid @RequestBody TourOperatorDTO dto) {
        logger.info("Richiesta POST ricevuta per la creazione del tour operator: {}", dto);
        try {
            tourOperatorService.processTourOperator(dto, false);
            logger.info("Tour operator creato con successo.");
            return ResponseEntity.ok("Tour operator creato con successo.");
        } catch (Exception e) {
            logger.error("Errore durante la creazione del tour operator", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore durante la creazione del tour operator.");
        }
    }

    @PutMapping
    public ResponseEntity<String> updateTourOperator(@Valid @RequestBody TourOperatorDTO dto) {
        logger.info("Richiesta PUT ricevuta per l'aggiornamento del tour operator: {}", dto);
        try {
            tourOperatorService.processTourOperator(dto, true);
            logger.info("Tour operator aggiornato con successo.");
            return ResponseEntity.ok("Tour operator aggiornato con successo.");
        } catch (Exception e) {
            logger.error("Errore durante l'aggiornamento del tour operator", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore durante l'aggiornamento del tour operator.");
        }
    }
}

