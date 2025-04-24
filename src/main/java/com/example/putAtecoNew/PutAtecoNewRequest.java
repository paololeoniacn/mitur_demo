package com.example.putAtecoNew;

import com.example.putAccommodation.ConsentsType;
import com.example.putAccommodation.ContactType;
import com.example.putAccommodation.LegalEntityType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.util.List;

@Data
public class PutAtecoNewRequest {

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
    private String country;
    private String streetName;
    private String streetNumber;
    private String alternativeAddress;
    @NotBlank
    private String primaryAtecoCod;
    @NotBlank
    private String phoneNumber;
    private String webSite;
    private String mailAddress;
    private String googleWebAddress;
    private String isoCertification;
    @NotBlank
    private String description;
    @NotEmpty
    private List<String> photos;
    private OpeningHoursType openingHoursType;

}
