package com.example.putrestaurant.service;

import com.example.putrestaurant.dto.RestaurantDTO;
import org.springframework.stereotype.Service;

@Service
public class S3AemUploaderService {
    public void upload(RestaurantDTO dto, String path) {
        // logica upload su S3/AEM --> decidere se chiamare il servizio specifico di upload oppure implementare qui la logica
    }
}
