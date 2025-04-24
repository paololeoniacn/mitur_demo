package com.example.putAtecoNew;

import com.example.demo.SlugifyService;
import com.example.uploads3aem.S3Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class RenderPeriodsTest {

    private PutAtecoNewService putAtecoNewService;

    @BeforeEach
    public void setUp() {
        S3Service s3Service = Mockito.mock(S3Service.class);
        SlugifyService slugifyService = Mockito.mock(SlugifyService.class);
        ObjectMapper objectMapper = new ObjectMapper();

        putAtecoNewService = new PutAtecoNewService(s3Service, slugifyService, objectMapper);
    }

    @Test
    public void testMapToRenderAEM() {
        PutAtecoNewRequest request = new PutAtecoNewRequest();
        RenderAtecoNewAEM render = new RenderAtecoNewAEM();
        OpeningHoursType openingHours = new OpeningHoursType();

        HoursType mondayHours = new HoursType();
        mondayHours.setStart("08:00");
        mondayHours.setEnd("12:00");
        openingHours.setMon(List.of(mondayHours));

        request.setOpeningHoursType(openingHours);

        RenderAtecoNewAEM result = putAtecoNewService.renderPeriods(request, render);

        List<PeriodsType> periodsMon = result.getPeriodsMon();
        assertNotNull(periodsMon);
        assertEquals(1, periodsMon.size());

        PeriodsType mondayPeriods = periodsMon.get(0);
        List<PeriodType> openPeriods = mondayPeriods.getOpen();
        List<PeriodType> closePeriods = mondayPeriods.getClose();

        assertEquals("08:00", openPeriods.get(0).getTime());
        assertEquals("12:00", closePeriods.get(0).getTime());
        assertEquals("1", openPeriods.get(0).getDay());
        assertEquals("1", closePeriods.get(0).getDay());
    }
}
