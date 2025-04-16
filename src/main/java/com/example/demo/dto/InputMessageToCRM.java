package com.example.demo.dto;

import lombok.Data;

@Data
public class InputMessageToCRM {
    private String type;
    private String method;
    private String identifier;
    private String currentTimeStamp;
    private String jsonRequest;
    private String trackingId;
}
