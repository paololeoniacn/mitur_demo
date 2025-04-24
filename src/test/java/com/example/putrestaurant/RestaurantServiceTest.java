package com.example.putrestaurant;

import com.example.putrestaurant.client.NormalizeNameClient;
import com.example.putrestaurant.dto.RestaurantDTO;
import com.example.putrestaurant.service.RestaurantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceTest {

    @Mock
    private NormalizeNameClient normalizeNameClient;

    @InjectMocks
    private RestaurantService restaurantService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(restaurantService, "initialPathRestaurant", "/initial/path/restaurant/");
    }


    @Test
    void testBuildPath_ShouldReturnCorrectPath_ForInsert() {
        // Arrange
        RestaurantDTO dto = new RestaurantDTO();
        dto.setName("Test Ristorante");
        dto.setCity("Milano");
        dto.setRegion("Lombardia");

        when(normalizeNameClient.normalize("Milano")).thenReturn("milano");
        when(normalizeNameClient.normalize("Lombardia")).thenReturn("lombardia");
        when(normalizeNameClient.normalize("Test Ristorante")).thenReturn("test-ristorante");

        // Act
        String path = restaurantService.buildPath(dto, false);

        // Assert
        assertTrue(path.contains("destination_put_test-ristorante_restaurant_"));
        assertTrue(path.startsWith("/initial/path/restaurant/lombardia/milano/test-ristorante/"));
    }

    @Test
    void testBuildPath_ShouldTruncateNameLongerThan80Chars() {
        // Arrange
        String longName = "RistoranteMoltoLungoConUnNomeEstremamenteEstesoCheSuperaGliOttantaCaratteri1234567890";
        RestaurantDTO dto = new RestaurantDTO();
        dto.setName(longName);
        dto.setCity("Torino");
        dto.setRegion("Piemonte");

        when(normalizeNameClient.normalize(anyString())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        String path = restaurantService.buildPath(dto, true);

        // Assert
        assertTrue(path.contains("destination_update_" + longName.substring(0, 80) + "_restaurant_"));
    }
}

