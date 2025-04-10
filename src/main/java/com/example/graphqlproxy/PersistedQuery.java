package com.example.graphqlproxy;

import lombok.Getter;

import java.util.Map;

/**
 * Modello dei dati per definire i dettagli della chiamata HTTP verso GraphQL.
 */
@Getter
public class PersistedQuery {
    private String method;
    private String requestURI;
    private String postData;
    private Headers headers;

    public void setMethod(String method) {
        this.method = method;
    }

    public void setRequestURI(String requestURI) {
        this.requestURI = requestURI;
    }

    public void setPostData(String postData) {
        this.postData = postData;
    }

    public void setHeaders(Headers headers) {
        this.headers = headers;
    }

    // Classe interna che rappresenta gli headers della richiesta
    @Getter
    public static class Headers {
        private String accept;
        private String contentType;
        private Map<String, String> dynamic;

        public void setAccept(String accept) {
            this.accept = accept;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        public void setDynamic(Map<String, String> dynamic) {
            this.dynamic = dynamic;
        }
    }
}