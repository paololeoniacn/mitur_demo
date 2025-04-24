package com.example.puttouroperator.service;

import com.example.puttouroperator.dto.TourOperatorDTO;
import org.springframework.stereotype.Service;

@Service
public class S3AemUploaderService {
    public void upload(TourOperatorDTO dto, String path) {
        // logica upload su S3/AEM --> decidere se chiamare il servizio specifico di upload oppure implementare qui la logica
    }
}
