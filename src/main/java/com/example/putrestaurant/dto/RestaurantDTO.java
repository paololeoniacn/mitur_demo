package com.example.putrestaurant.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class RestaurantDTO {

    // aggiungere validazione ad es. @notNull dove necessario (al controller la validazione è già stata aggiunta)
    private String identifier;
    private String name;
    private String fiscalCod;
    private String businessName;
    private String mainActivity;
    private String provinceCCIAA;
    private String reaCCIAACod;
    private String legalForm;
    private ConsentsDTO consentsDTO;
    private ContactDTO companyContactDTO;
    private ContactDTO editorContactDTO;
    private LegalEntityDTO legalEntityDTO;
    private String fullAddress;
    private String city;
    private String postalCode;
    private String province;
    private String region;
    private String country;
    private String streetName;
    private String streetNumber;
    private String primaryAtecoCod;
    private String phoneNumber;
    private String mailAddress;
    private String isoCertification;
    private String description;
    private List<String> photos;
    private String mainType;
    private Map<String, List<OpeningHourDTO>> opening_hours;
    private List<String> ambienceType;
    private List<String> listOfService;
}
