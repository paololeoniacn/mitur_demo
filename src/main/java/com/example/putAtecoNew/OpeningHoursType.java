package com.example.putAtecoNew;

import lombok.Data;

import java.util.List;

@Data
public class OpeningHoursType {
    private List<HoursType> mon;
    private List<HoursType> tue;
    private List<HoursType> wed;
    private List<HoursType> thu;
    private List<HoursType> fri;
    private List<HoursType> sat;
    private List<HoursType> sun;
}
