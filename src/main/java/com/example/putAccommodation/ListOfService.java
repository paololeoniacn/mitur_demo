package com.example.putAccommodation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ListOfService {
    WIFI_GRATUITO("Connessione WiFi gratuita"),
    NAVETTA_AEROPORTUALE("Navetta aeroportuale"),
    RECEPTION_24H("Reception 24 ore su 24"),
    RISTORANTE("Ristorante"),
    SALA_FITNESS("Sala Fitness"),
    SPA_CENTRO_BENESSERE("Spa & centro benessere"),
    PISCINA("Piscina"),
    SALA_CONFERENZE("Sala Conferenze"),
    DEPOSITO_BAGAGLI("Deposito Bagagli"),
    PARCHEGGIO("Parcheggio"),
    CAMERE_PER_FAMIGLIE("Camere per famiglie"),
    ASCENSORE("Ascensore"),
    ANIMALI_AMMESSI("Animali Ammessi"),
    SERVIZIO_IN_CAMERA("Servizio in camera"),
    CAMERE_DISABILI("Camere/struttura per ospiti disabili");

    private final String value;

    ListOfService(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static ListOfService fromValue(String value) {
        for (ListOfService service : values()) {
            if (service.value.equalsIgnoreCase(value)) {
                return service;
            }
        }
        throw new IllegalArgumentException("Servizio non valido: " + value);
    }
}
