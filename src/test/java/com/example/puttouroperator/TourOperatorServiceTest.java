package com.example.puttouroperator;

import com.example.puttouroperator.client.NormalizeNameClient;
import com.example.puttouroperator.dto.TourOperatorDTO;
import com.example.puttouroperator.service.TourOperatorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TourOperatorServiceTest {

    @Mock
    private NormalizeNameClient normalizeNameClient;

    @InjectMocks
    private TourOperatorService tourOperatorService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(tourOperatorService, "initialPathTourOperator", "s3://bucket/tour-operators");
        ReflectionTestUtils.setField(tourOperatorService, "initialPathTouristGuides", "s3://bucket/tourist-guides");

        OffsetDateTime pastDate = OffsetDateTime.now().minusDays(1);
        ReflectionTestUtils.setField(tourOperatorService, "startDateRaw", pastDate.toString());
    }

    @Test
    void shouldGenerateTouristGuidesPath_whenAtecoCodeMatchesAfterStartDate() {
        TourOperatorDTO dto = getTourOperatorDTO("79.90.01", "Lazio", "Roma", "Tour Expert");

        when(normalizeNameClient.normalize("Lazio")).thenReturn("lazio");
        when(normalizeNameClient.normalize("Roma")).thenReturn("roma");
        when(normalizeNameClient.normalize("Tour Expert")).thenReturn("tour-expert");

        String path = tourOperatorService.checkAtecoAndGeneratePath(dto, false);

        assertPathContains(path, "s3://bucket/tourist-guides", "lazio", "roma", "tour-expert", "tourist-guides");
        assertTrue(path.contains("destination_put_tour-expert_tourist-guides_"));
    }

    @Test
    void shouldGenerateTourOperatorPath_whenAtecoCodeDoesNotMatch() {
        TourOperatorDTO dto = getTourOperatorDTO("55.10.00", "Lombardia", "Milano", "Milano Travels");

        when(normalizeNameClient.normalize("Lombardia")).thenReturn("lombardia");
        when(normalizeNameClient.normalize("Milano")).thenReturn("milano");
        when(normalizeNameClient.normalize("Milano Travels")).thenReturn("milano-travels");

        String path = tourOperatorService.checkAtecoAndGeneratePath(dto, true);

        assertPathContains(path, "s3://bucket/tour-operators", "lombardia", "milano", "milano-travels", "tour-operators");
        assertTrue(path.contains("destination_update_milano-travels_tour-operators_"));
    }

    @Test
    void shouldTruncateNameIfTooLong() {
        String longName = "A".repeat(100);
        TourOperatorDTO dto = getTourOperatorDTO("79.90.01", "Toscana", "Firenze", longName);

        when(normalizeNameClient.normalize(any())).thenAnswer(i -> i.getArguments()[0]);

        String path = tourOperatorService.checkAtecoAndGeneratePath(dto, false);

        String expectedPrefix = "destination_put_" + longName.substring(0, 80);
        assertTrue(path.contains(expectedPrefix));
    }

    @Test
    void shouldUseCorrectActionForIsUpdateTrue() {
        TourOperatorDTO dto = getTourOperatorDTO("79.90.03", "Sicilia", "Palermo", "Island Adventures");

        when(normalizeNameClient.normalize(any())).thenAnswer(i -> i.getArguments()[0]);

        String path = tourOperatorService.checkAtecoAndGeneratePath(dto, true);

        assertTrue(path.contains("destination_update_"));
    }

    private static TourOperatorDTO getTourOperatorDTO(String primaryAtecoCod, String region, String city, String name) {
        TourOperatorDTO dto = new TourOperatorDTO();
        dto.setPrimaryAtecoCod(primaryAtecoCod);
        dto.setRegion(region);
        dto.setCity(city);
        dto.setName(name);
        return dto;
    }

    private void assertPathContains(String fullPath, String... parts) {
        for (String part : parts) {
            assertTrue(fullPath.contains(part), "Path should contain: " + part);
        }
    }
}
