package com.example.putAccommodation;

import jakarta.xml.bind.annotation.*;
import lombok.Data;

import java.util.List;

@Data
@XmlRootElement(name = "requestPutAccommodation", namespace = "http://www.example.org/putAccommodation")
@XmlAccessorType(XmlAccessType.FIELD)
public class PutAccommodationRequest {

    @XmlElement(namespace = "http://www.example.org/putAccommodation")
    private String identifier;
    @XmlElement(namespace = "http://www.example.org/putAccommodation")
    private String name;
    @XmlElement(namespace = "http://www.example.org/putAccommodation")
    private String vatCod;
    @XmlElement(namespace = "http://www.example.org/putAccommodation")
    private String fiscalCod;
    @XmlElement(namespace = "http://www.example.org/putAccommodation")
    private String businessName;
    @XmlElement(namespace = "http://www.example.org/putAccommodation")
    private String mainActivity;
    @XmlElement(namespace = "http://www.example.org/putAccommodation")
    private String provinceCCIAA;
    @XmlElement(namespace = "http://www.example.org/putAccommodation")
    private String reaCCIAACod;
    @XmlElement(namespace = "http://www.example.org/putAccommodation")
    private String legalForm;

    @XmlElement(namespace = "http://www.example.org/putRequest")
    private ConsentsType consents;

    @XmlElement(namespace = "http://www.example.org/putRequest")
    private ContactType companyContact;

    @XmlElement(namespace = "http://www.example.org/putRequest")
    private ContactType editorContact;

    @XmlElement(namespace = "http://www.example.org/putRequest")
    private LegalEntityType legalEntity;

    @XmlElement(namespace = "http://www.example.org/putAccommodation")
    private String fullAddress;
    @XmlElement(namespace = "http://www.example.org/putAccommodation")
    private String city;
    @XmlElement(namespace = "http://www.example.org/putAccommodation")
    private String postalCode;
    @XmlElement(namespace = "http://www.example.org/putAccommodation")
    private String region;
    @XmlElement(namespace = "http://www.example.org/putAccommodation")
    private String province;
    @XmlElement(namespace = "http://www.example.org/putAccommodation")
    private String primaryAtecoCod;
    @XmlElement(namespace = "http://www.example.org/putAccommodation")
    private String country;
    @XmlElement(namespace = "http://www.example.org/putAccommodation")
    private String streetName;
    @XmlElement(namespace = "http://www.example.org/putAccommodation")
    private String streetNumber;
    @XmlElement(namespace = "http://www.example.org/putAccommodation")
    private String phoneNumber;
    @XmlElement(namespace = "http://www.example.org/putAccommodation")
    private String webSite;
    @XmlElement(namespace = "http://www.example.org/putAccommodation")
    private String mailAddress;
    @XmlElement(namespace = "http://www.example.org/putAccommodation")
    private String googleWebAddress;
    @XmlElement(namespace = "http://www.example.org/putAccommodation")
    private String isoCertification;


    @XmlElement(name = "photos", namespace = "http://www.example.org/putAccommodation")
    private List<String> photos;

    @XmlElement(namespace = "http://www.example.org/putAccommodation")
    private String accomodationType;
    @XmlElement(namespace = "http://www.example.org/putAccommodation")
    private String rating;
    @XmlElement(namespace = "http://www.example.org/putAccommodation")
    private String description;

    @XmlElement(name = "roomService", namespace = "http://www.example.org/putAccommodation")
    private List<String> roomService;

    @XmlElement(name = "paymentMethods", namespace = "http://www.example.org/putAccommodation")
    private List<String> paymentMethods;

    @XmlElement(name = "listOfService", namespace = "http://www.example.org/putAccommodation")
    private List<String> listOfService;

    @XmlElement(namespace = "http://www.example.org/putAccommodation")
    private String checkIn;
    @XmlElement(namespace = "http://www.example.org/putAccommodation")
    private String checkOut;
    @XmlElement(namespace = "http://www.example.org/putAccommodation")
    private String hotelChain;

    @XmlElement(name = "cin", namespace = "http://www.example.org/putAccommodation")
    private List<String> cin;

}
