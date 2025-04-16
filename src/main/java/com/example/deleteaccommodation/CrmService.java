package com.example.deleteaccommodation;

import org.springframework.stereotype.Service;

@Service
class CrmService {
    public void notifyCRM(String identifier) {
        // TODO: Chiamata REST o messaggio a un sistema CRM
        System.out.println("Notifying CRM about deletion of ID: " + identifier);
    }
}