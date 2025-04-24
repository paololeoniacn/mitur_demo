package com.example.puttouroperator.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class TourOperatorDTO {

    // aggiungere validazione ad es. @notNull dove necessario (al controller la validazione è già stata aggiunta)
    private String identifier;
    private String name;
    private String vatCod;
    private String fiscalCod;
    private String businessName;
    private String mainActivity;
    private String provinceCCIAA;
    private String reaCCIAACod;
    private String legalForm;
    private ContactDTO companyContact;
    private LegalEntityDTO legalEntityDTO;
    private ContactDTO editorContact;
    private String fullAddress;
    private String city;
    private String postalCode;
    private String region;
    private String country;
    private String province;
    private String streetName;
    private String streetNumber;
    private String phoneNumber;
    private String webSite;
    private String mailAddress;
    private String isoCertification;
    private String primaryAtecoCod;
    private List<String> photos;
    private Map<String, List<OpeningHourDTO>> opening_hours;
    private String description;
    private List<String> listOfService;


}
