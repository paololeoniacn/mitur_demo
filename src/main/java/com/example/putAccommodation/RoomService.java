package com.example.putAccommodation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum RoomService {
    CUCINA_ANGOLO_COTTURA("Cucina/angolo cottura"),
    BAGNO_PRIVATO("Bagno privato"),
    ARIA_CONDIZIONATA("Aria condizionata"),
    RISCALDAMENTO("Riscaldamento"),
    CULLA("Culla"),
    SCRIVANIA("Scrivania"),
    VASCA("Vasca"),
    TERRAZZA_BALCONE("Terrazza/Balcone"),
    TV_SCHERMO_PIATTO("TV a schermo piatto"),
    LAVATRICE("Lavatrice"),
    ASCIUGACAPELLI("Asciugacapelli"),
    VISTA("Vista"),
    BOLLITORE_ELETTRICO_MACCHINA_CAFFE("Bollitore elettrico/Macchina da caffè"),
    CAMERA_OSPITI_DISABILI("Camera per ospiti disabili");

    private final String value;

    RoomService(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static RoomService fromValue(String value) {
        for (RoomService service : RoomService.values()) {
            if (service.value.equalsIgnoreCase(value)) {
                return service;
            }
        }
        throw new IllegalArgumentException("Valore non valido: " + value);
    }
}
