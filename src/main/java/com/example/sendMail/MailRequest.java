package com.example.sendMail;

import jakarta.validation.constraints.Email;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@XmlRootElement(name = "sendMailRequest", namespace = "http://www.example.org/sendMail")
@XmlAccessorType(XmlAccessType.FIELD)
public class MailRequest {

    @XmlElement(namespace = "http://www.example.org/sendMail")
    private String from;
    @XmlElement(namespace = "http://www.example.org/sendMail")
    private List<@Email String> to;
    @XmlElement(namespace = "http://www.example.org/sendMail")
    private List<@Email String> cc;
    @XmlElement(namespace = "http://www.example.org/sendMail")
    private List<@Email String> bcc;
    @XmlElement(namespace = "http://www.example.org/sendMail")
    private String replyTo;
    @XmlElement(namespace = "http://www.example.org/sendMail")
    private String subject;
    @XmlElement(namespace = "http://www.example.org/sendMail")
    private Date sentDate;
    @XmlElement(namespace = "http://www.example.org/sendMail")
    private String bodyText;
    @XmlElement(namespace = "http://www.example.org/sendMail")
    private String contentType;
}
