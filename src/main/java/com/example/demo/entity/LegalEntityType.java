package com.example.demo.entity;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class LegalEntityType {
    private String country;
    private String province;
    private String city;
    private String zipCode;
    private String address;
    private String civicNumber;
    private String emailPEC;
}
