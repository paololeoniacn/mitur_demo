package com.example.putAtecoNew;

import lombok.Data;

import java.util.List;

@Data
public class RenderAtecoNewAEM {
    private String identifier;
    private String fiscalCod;
    private String pIva;
    private String status;
    private String insegna;
    private String officialName;
    private String language;
    private String fullAddress;
    private String city;
    private String postalCode;
    private String province;
    private String region;
    private String country;
    private String streetName;
    private String streetNumber;
    private String alternativeAddress;
    private String primaryAtecoCode;
    private String category;
    private String primaryTag;
    private String destinationType;
    private String poiTelephoneNumber;
    private String poiWebsiteUrl;
    private String poiEmail;
    private String websiteGoogle;
    private String isoCert;
    private List<PeriodsType> periodsMon;
    private List<PeriodsType> periodsTue;
    private List<PeriodsType> periodsWed;
    private List<PeriodsType> periodsThu;
    private List<PeriodsType> periodsFri;
    private List<PeriodsType> periodsSat;
    private List<PeriodsType> periodsSun;
    private String description;
    private List<String> images;
}
