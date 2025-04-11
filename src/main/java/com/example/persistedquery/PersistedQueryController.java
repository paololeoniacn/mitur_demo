package com.example.persistedquery;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/persistedQuery")
public class PersistedQueryController {

    private static final Logger logger = LogManager.getLogger(PersistedQueryController.class);
    @Value("${external.api.url}")
    private String externalApiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/{param}")
    public ResponseEntity<String> forwardRequest(@PathVariable String param,
                                                 @MatrixVariable(pathVar = "param") Map<String, String> matrixVars) {

        logger.info("param: {}", param);
        logger.info("matrix values: {}", matrixVars);

        // Costruisce l'URL finale mantenendo i parametri con ';'
        String targetUrl = buildMatrixUrl(externalApiUrl, param, matrixVars);

        logger.info("url: {}", targetUrl);

        // Inoltra la richiesta al servizio esterno
        ResponseEntity<String> response = restTemplate.getForEntity(targetUrl, String.class);

        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }

    public String buildMatrixUrl(String baseUrl, String queryName, Map<String, String> matrixParams) {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(baseUrl).append("/").append(queryName);

        if (matrixParams != null && !matrixParams.isEmpty()) {
            for (Map.Entry<String, String> entry : matrixParams.entrySet()) {
                urlBuilder.append(";")
                        .append(entry.getKey())
                        .append("=")
                        .append(entry.getValue());
            }
            urlBuilder.append(";"); // <- aggiunge il ; finale
        }

        return urlBuilder.toString();
    }

}


