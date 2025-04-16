package com.example.putAtecoNew;

import lombok.Data;

import java.util.List;

@Data
public class PeriodsType {
    private List<PeriodType> open;
    private List<PeriodType> close;
}
