package com.example.deleteaccommodation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.io.IOException;

@RestController
@RequestMapping("/accommodation")
public class AccommodationController {

    @Autowired
    private AccommodationService accommodationService;

    @Autowired
    private CrmService crmService;

    @PostMapping("/delete")
    public ResponseEntity<?> deleteAccommodation(
            @RequestHeader("X-Mashery-Application") String masheryHeader,
            @RequestBody DeleteAccommodationRequest request
    ) throws IOException {
        String partner = extractPartnerFromHeader(masheryHeader);
        String identifier = request.getIdentifier();
        String type = request.getType();
        String primaryAtecoCod = request.getPrimaryAtecoCod();
        String startDateAtecoNew = request.getStartDateAtecoNew();

        accommodationService.deleteAccommodation(identifier, type, primaryAtecoCod, startDateAtecoNew);
        crmService.notifyCRM(identifier);

        return ResponseEntity.ok(Map.of("deletedId", identifier));
    }

    private String extractPartnerFromHeader(String headerValue) {
        return headerValue.split("TeamApp")[0];
    }
}
