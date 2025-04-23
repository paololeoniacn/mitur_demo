package com.example.putAccommodation;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

@Data
public class ConsentsType {

    private boolean allowEventCommunication;
    private String allowEventCommunicationDate;
    private boolean allowPromotionalInitiatives;
    private String allowPromotionalInitiativesDate;

}
