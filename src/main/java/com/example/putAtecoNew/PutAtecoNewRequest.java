package com.example.putAtecoNew;

import com.example.putAccommodation.ConsentsType;
import com.example.putAccommodation.ContactType;
import com.example.putAccommodation.LegalEntityType;
import jakarta.xml.bind.annotation.*;
import lombok.Data;

import java.util.List;

@Data
@XmlRootElement(name = "requestPutAtecoNew", namespace = "http://www.example.org/putAtecoNew")
@XmlAccessorType(XmlAccessType.FIELD)
public class PutAtecoNewRequest {

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
    private String country;
    private String streetName;
    private String streetNumber;
    private String alternativeAddress;
    private String primaryAtecoCod;
    private String phoneNumber;
    private String webSite;
    private String mailAddress;
    private String googleWebAddress;
    private String isoCertification;
    private String description;

    @XmlElementWrapper(name = "photos")
    @XmlElement(name = "photo")
    private List<String> photos;

    @XmlElement(namespace = "http://www.example.org/putRequest")
    private OpeningHoursType openingHoursType;

}
