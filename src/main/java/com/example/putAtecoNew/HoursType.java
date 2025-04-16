package com.example.putAtecoNew;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "hoursType", propOrder = {
        "start",
        "end"
})
public class HoursType {

    @XmlElement(namespace = "http://www.example.org/putRequest", required = true)
    private String start;

    @XmlElement(namespace = "http://www.example.org/putRequest", required = true)
    private String end;
}
