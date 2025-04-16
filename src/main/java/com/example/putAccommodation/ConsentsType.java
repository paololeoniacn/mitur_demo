package com.example.putAccommodation;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class ConsentsType {
    private Boolean allowEventCommunication;
    private String allowEventCommunicationDate;
    private Boolean allowPromotionalInitiatives;
    private String allowPromotionalInitiativesDate;
}
