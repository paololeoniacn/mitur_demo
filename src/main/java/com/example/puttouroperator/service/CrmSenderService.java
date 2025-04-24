package com.example.puttouroperator.service;

import com.example.puttouroperator.dto.TourOperatorDTO;
import org.springframework.stereotype.Service;

@Service
public class CrmSenderService {
    public void send(TourOperatorDTO dto) {
        // logica invio dati al CRM (in postman vedo url e autenticazione oauth2)
    }
}
