package com.example.putAccommodation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Data;

public enum AccomodationType {
    HOTEL("Hotel"),
    OSTELLO("Ostello"),
    APPARTAMENTO("Appartamento"),
    APARTHOTEL("Aparthotel"),
    AFFITTACAMERE("Affittacamere"),
    COTTAGE("Cottage"),
    VILLA("Villa"),
    BUNGALOW("Bungalow"),
    CAMPEGGIO("Campeggio"),
    GLAMPINGS("Glampings"),
    PENSIONE("Pensione"),
    BED_AND_BREAKFAST("Bed & Breakfast"),
    ALLOGGIO_AGRITURISTICO("Alloggio agrituristico/Agriturismo");

    private final String value;

    AccomodationType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static AccomodationType fromValue(String value) {
        for (AccomodationType type : values()) {
            if (type.value.equalsIgnoreCase(value)) return type;
        }
        throw new IllegalArgumentException("Valore non valido: " + value);
    }
}
