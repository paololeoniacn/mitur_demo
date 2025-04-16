package com.example.putAccommodation;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Data;

@Data
@XmlType(namespace = "http://www.example.org/putRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class ConsentsType {
    @XmlElement(namespace = "http://www.example.org/putRequest")
    private Boolean allowEventCommunication;
    @XmlElement(namespace = "http://www.example.org/putRequest")
    private String allowEventCommunicationDate;
    @XmlElement(namespace = "http://www.example.org/putRequest")
    private Boolean allowPromotionalInitiatives;
    @XmlElement(namespace = "http://www.example.org/putRequest")
    private String allowPromotionalInitiativesDate;
}
