package com.example.putrestaurant.dto;

import lombok.Data;

@Data
public class LegalEntityDTO {
    private String country;
    private String province;
    private String city;
    private String zipCode;
    private String address;
    private String civicNumber;
    private String emailPEC;
}
