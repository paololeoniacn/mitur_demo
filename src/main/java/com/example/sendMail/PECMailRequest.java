package com.example.sendMail;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PECMailRequest {
    @NotNull
    @Email
    private String to;
    @NotNull
    private String subject;
    @NotNull
    private String body;
    private String fullName;

}
