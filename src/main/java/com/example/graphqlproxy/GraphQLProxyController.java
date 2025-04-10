package com.example.graphqlproxy;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/input")
public class GraphQLProxyController {

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Questo endpoint riceve una richiesta con i dettagli per chiamare un server GraphQL,
     * inoltra la chiamata e restituisce la risposta.
     */
    @PostMapping
    public ResponseEntity<String> startProcess(@RequestBody InfoLog infoLog) {
        // Estrae i dettagli della query da eseguire
        PersistedQuery query = infoLog.getPersistedQuery();

        // Prepara gli header HTTP da inviare
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", query.getHeaders().getAccept());
        headers.setContentType(MediaType.valueOf(query.getHeaders().getContentType()));

        // Aggiunge eventuali header dinamici personalizzati
        if (query.getHeaders().getDynamic() != null) {
            query.getHeaders().getDynamic().forEach(headers::set);
        }

        // Crea il corpo della richiesta HTTP
        HttpEntity<String> entity = new HttpEntity<>(query.getPostData(), headers);

        // Invia la richiesta al server GraphQL usando il metodo e l'URI specificati
        ResponseEntity<String> response = restTemplate.exchange(
                query.getRequestURI(),
                HttpMethod.valueOf(query.getMethod()),
                entity,
                String.class
        );

        // Costruisce la risposta da restituire al chiamante con lo stesso status
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);

        return ResponseEntity
                .status(response.getStatusCode())
                .headers(responseHeaders)
                .body(response.getBody());
    }
}