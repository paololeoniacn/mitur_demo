package com.example.putAtecoNew;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Data;

import java.util.List;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class OpeningHoursType {
    @XmlElement(namespace = "http://www.example.org/putRequest")
    private List<HoursType> mon;

    @XmlElement(namespace = "http://www.example.org/putRequest")
    private List<HoursType> tue;

    @XmlElement(namespace = "http://www.example.org/putRequest")
    private List<HoursType> wed;

    @XmlElement(namespace = "http://www.example.org/putRequest")
    private List<HoursType> thu;

    @XmlElement(namespace = "http://www.example.org/putRequest")
    private List<HoursType> fri;

    @XmlElement(namespace = "http://www.example.org/putRequest")
    private List<HoursType> sat;

    @XmlElement(namespace = "http://www.example.org/putRequest")
    private List<HoursType> sun;
}
