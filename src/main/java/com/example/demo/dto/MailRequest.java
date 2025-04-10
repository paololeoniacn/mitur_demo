package com.example.demo.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


import java.util.Date;
import java.util.List;

@Data
public class MailRequest {
    @Email
    private String from;
    @NotNull
    private List<@Email String> to;
    private List<@Email String> cc;
    private List<@Email String> bcc;
    private List<@Email String> replyTo;
    private String subject;
    private Date sentDate;
    private String bodyText;
    private String contentType;
}
