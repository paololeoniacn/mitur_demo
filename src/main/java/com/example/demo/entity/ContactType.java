package com.example.demo.entity;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class ContactType {
    private String name;
    private String surname;
    private String email;
    private String fiscalCode;
    private String jobTitle;
    private String phoneNumber;
    private String role;
}
