package com.example.putAccommodation;

import lombok.Data;

@Data
public class LegalEntityType {
    private String country;
    private String province;
    private String city;
    private String zipCode;
    private String address;
    private String civicNumber;
    private String emailPEC;
}
