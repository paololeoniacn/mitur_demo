package com.example.putAccommodation;

import lombok.Data;

import java.util.List;

@Data
public class RenderAccommodationAEM {
    private String identifier;
    private String fiscalCod;
    private String pIva;
    private String status;
    private String insegna;
    private String officialName;
    private String language;
    private String country;
    private String province;
    private String city;
    private String postalCode;
    private String streetName;
    private String streetNumber;
    private String fullAddress;
    private String region;
    private String category;
    private String primaryTag;
    private String destinationType;
    private String poiTelephoneNumber;
    private String poiWebsiteUrl;
    private String poiEmail;
    private String websiteGoogle;
    private String description;
    private String isoCert;
    private List<String> images; // lista dei path su cui sono state salvate le immagini
    private String type;
    private String starRating;
    private List<String> listOfServices;
    private List<String> roomListOfServices;
    private List<String> paymentMethods;
    private String checkIn;
    private String checkOut;
    private String hotelChain;
    private List<String> cin;

}
