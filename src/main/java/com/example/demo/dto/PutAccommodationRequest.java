package com.example.demo.dto;

import com.example.demo.entity.ConsentsType;
import com.example.demo.entity.ContactType;
import com.example.demo.entity.LegalEntityType;
import jakarta.xml.bind.annotation.*;
import lombok.Data;

import java.util.List;

@Data
@XmlRootElement(name = "requestPutAccommodation", namespace = "http://www.example.org/putAccommodation")
@XmlAccessorType(XmlAccessType.FIELD)
public class PutAccommodationRequest {

    private String identifier;
    private String name;
    private String vatCod;
    private String fiscalCod;
    private String businessName;
    private String mainActivity;
    private String provinceCCIAA;
    private String reaCCIAACod;
    private String legalForm;

    @XmlElement(namespace = "http://www.example.org/putRequest")
    private ConsentsType consents;

    @XmlElement(namespace = "http://www.example.org/putRequest")
    private ContactType companyContact;

    @XmlElement(namespace = "http://www.example.org/putRequest")
    private ContactType editorContact;

    @XmlElement(namespace = "http://www.example.org/putRequest")
    private LegalEntityType legalEntity;

    private String fullAddress;
    private String city;
    private String postalCode;
    private String region;
    private String province;
    private String primaryAtecoCod;
    private String country;
    private String streetName;
    private String streetNumber;
    private String phoneNumber;
    private String webSite;
    private String mailAddress;
    private String googleWebAddress;
    private String isoCertification;

    @XmlElementWrapper(name = "photos")
    @XmlElement(name = "photo")
    private List<String> photos;

    private String accomodationType;
    private String rating;
    private String description;

    @XmlElementWrapper(name = "roomService")
    @XmlElement(name = "service")
    private List<String> roomService;

    @XmlElementWrapper(name = "paymentMethods")
    @XmlElement(name = "method")
    private List<String> paymentMethods;

    @XmlElementWrapper(name = "listOfService")
    @XmlElement(name = "service")
    private List<String> listOfService;

    private String checkIn;
    private String checkOut;
    private String hotelChain;

    @XmlElementWrapper(name = "cin")
    @XmlElement(name = "code")
    private List<String> cin;

}
