package com.example.putAccommodation;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class PutAccommodationRequest {

    @NotBlank
    private String identifier;
    @NotBlank
    private String name;
    private String vatCod;
    @NotBlank
    private String fiscalCod;
    private String businessName;
    private String mainActivity;
    private String provinceCCIAA;
    private String reaCCIAACod;
    private String legalForm;
    private ConsentsType consents;
    private ContactType companyContact;
    private ContactType editorContact;
    private LegalEntityType legalEntity;
    @NotBlank
    private String fullAddress;
    @NotBlank
    private String city;
    private String postalCode;
    @NotBlank
    private String region;
    private String province;
    private String primaryAtecoCod;
    private String country;
    private String streetName;
    private String streetNumber;
    @NotBlank
    private String phoneNumber;
    private String webSite;
    private String mailAddress;
    private String googleWebAddress;
    private String isoCertification;
    @NotEmpty
    private List<String> photos;
    @NotNull
    private AccomodationType accomodationType;
    private String rating;
    @NotBlank
    private String description;
    private List<RoomService> roomService;
    private List<PaymentMethod> paymentMethods;
    @NotEmpty
    private List<ListOfService> listOfService;
    @NotBlank
    private String checkIn;
    @NotBlank
    private String checkOut;
    private String hotelChain;
    @NotEmpty
    private List<String> cin;

}
