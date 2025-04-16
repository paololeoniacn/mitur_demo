package com.example.putAccommodation;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Data;

@Data
@XmlType(namespace = "http://www.example.org/putRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class ContactType {
    @XmlElement(namespace = "http://www.example.org/putRequest")
    private String name;
    @XmlElement(namespace = "http://www.example.org/putRequest")
    private String surname;
    @XmlElement(namespace = "http://www.example.org/putRequest")
    private String email;
    @XmlElement(namespace = "http://www.example.org/putRequest")
    private String fiscalCode;
    @XmlElement(namespace = "http://www.example.org/putRequest")
    private String jobTitle;
    @XmlElement(namespace = "http://www.example.org/putRequest")
    private String phoneNumber;
    @XmlElement(namespace = "http://www.example.org/putRequest")
    private String role;
}
