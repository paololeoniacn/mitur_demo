package com.example.sendMail;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@Data
@XmlRootElement(name = "requestSendMailPEC", namespace = "http://www.example.org/sendMailPEC")
@XmlAccessorType(XmlAccessType.FIELD)
public class PECMailRequest {

    @XmlElement(namespace = "http://www.example.org/sendMailPEC")
    private String to;
    @XmlElement(namespace = "http://www.example.org/sendMailPEC")
    private String subject;
    @XmlElement(namespace = "http://www.example.org/sendMailPEC")
    private String body;
    @XmlElement(namespace = "http://www.example.org/sendMailPEC")
    private String fullName;
}
