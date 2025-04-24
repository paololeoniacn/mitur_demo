package com.example.putAccommodation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PaymentMethod {
    CARTA_DI_CREDITO("Carta di credito"),
    BONIFICO("Bonifico"),
    PAGAMENTO_IN_STRUTTURA("Pagamento in struttura");

    private final String value;

    PaymentMethod(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static PaymentMethod fromValue(String value) {
        for (PaymentMethod method : values()) {
            if (method.value.equalsIgnoreCase(value)) {
                return method;
            }
        }
        throw new IllegalArgumentException("Metodo di pagamento non valido: " + value);
    }
}
