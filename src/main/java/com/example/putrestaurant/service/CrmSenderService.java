package com.example.putrestaurant.service;

import com.example.putrestaurant.dto.RestaurantDTO;
import org.springframework.stereotype.Service;

@Service
public class CrmSenderService {
    public void send(RestaurantDTO dto) {
        // logica invio dati al CRM (in postman vedo url e autenticazione oauth2)
    }
}
