package com.example.putAccommodation;

import com.example.demo.SlugifyService;
import com.example.putAccommodation.dto.RenderAccommodationAEM;
import com.example.uploads3aem.S3Request;
import com.example.uploads3aem.S3Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PutAccommodationServiceTest {

    @Mock
    private S3Service s3Service;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private SlugifyService slugifyService;

    @InjectMocks
    private PutAccommodationService putAccommodationService;

    private PutAccommodationRequest testRequest;

    @BeforeEach
    void setUp() {
        testRequest = new PutAccommodationRequest();
        testRequest.setName("Test Hotel");
        testRequest.setRegion("Lombardy");
        testRequest.setCity("Milan");
        testRequest.setPhotos(Arrays.asList("http://example.com/photo1.jpg", "http://example.com/photo2.jpg"));
        testRequest.setIdentifier("test-id-123");
        testRequest.setFiscalCod("FISCAL123");
        testRequest.setFullAddress("Test Address");
        testRequest.setPhoneNumber("+123456789");
        testRequest.setDescription("Test description");
        testRequest.setCin(List.of("CIN123"));
        testRequest.setCheckIn("14:00");
        testRequest.setCheckOut("11:00");

        // Set enum values
        testRequest.setAccomodationType(AccomodationType.HOTEL); // Assuming this is your enum
        testRequest.setListOfService(Collections.singletonList(ListOfService.WIFI_GRATUITO)); // Example service
        testRequest.setRoomService(Collections.singletonList(RoomService.ARIA_CONDIZIONATA)); // Example room service
        testRequest.setPaymentMethods(Collections.singletonList(PaymentMethod.CARTA_DI_CREDITO)); // Example payment method
    }

    @Test
    void putAccommodation_shouldProcessRequestSuccessfully() throws Exception {
        // Arrange
        when(slugifyService.normalize(anyString())).thenReturn("normalized");
        when(objectMapper.writeValueAsString(any())).thenReturn("jsonString");

        try (MockedStatic<Utils> utilsMock = mockStatic(Utils.class)) {
            utilsMock.when(() -> Utils.downloadImage(anyString()))
                    .thenReturn(new byte[]{1, 2, 3});
            utilsMock.when(() -> Utils.pathBuilder(anyString(), anyString(), anyString(), anyString(), anyString(), anyString()))
                    .thenReturn("/mock/path/to/image.jpg");

            putAccommodationService.putAccommodation(testRequest);

            verify(s3Service, times(2)).uploadImage(any(), anyString());
            verify(s3Service).process(any(S3Request.class));
        }
    }

    @Test
    void postAccommodation_shouldProcessRequestSuccessfully() throws Exception {
        // Arrange
        when(slugifyService.normalize(anyString())).thenReturn("normalized");
        when(objectMapper.writeValueAsString(any())).thenReturn("jsonString");

        try (MockedStatic<Utils> utilsMock = mockStatic(Utils.class)) {
            // Mock the static methods
            utilsMock.when(() -> Utils.downloadImage(anyString()))
                    .thenReturn(new byte[]{1, 2, 3});
            utilsMock.when(() -> Utils.pathBuilder(anyString(), anyString(), anyString(), anyString(), anyString(), anyString()))
                    .thenReturn("/mock/path/to/image.jpg");

            // Act
            putAccommodationService.postAccommodation(testRequest);

            // Assert
            verify(s3Service, times(2)).uploadImage(any(), anyString());
            verify(s3Service).process(any(S3Request.class));
        }
    }

    @Test
    void renderAccommodation_shouldMapRequestToAEMCorrectly() {
        // Arrange
        List<String> imagePaths = Arrays.asList("/path/to/image1.jpg", "/path/to/image2.jpg");

        // Act
        RenderAccommodationAEM result = putAccommodationService.renderAccommodation(testRequest, imagePaths);

        // Assert
        assertNotNull(result);
        assertEquals(testRequest.getName(), result.getInsegna());
        assertEquals(testRequest.getName(), result.getOfficialName());
        assertEquals(testRequest.getCity(), result.getCity());
        assertEquals(testRequest.getRegion(), result.getRegion());
        assertEquals(imagePaths, result.getImages());
        assertEquals(testRequest.getAccomodationType().getValue(), result.getType());
        assertEquals(1, result.getListOfServices().size());
        assertEquals(1, result.getRoomListOfServices().size());
        assertEquals(1, result.getPaymentMethods().size());
    }


    @Test
    void renderJsonToString_shouldReturnJsonString() throws JsonProcessingException {
        // Arrange
        RenderAccommodationAEM aem = new RenderAccommodationAEM();
        String expectedJson = "{}";
        when(objectMapper.writeValueAsString(aem)).thenReturn(expectedJson);

        // Act
        String result = putAccommodationService.renderJsonToString(aem);

        // Assert
        assertEquals(expectedJson, result);
    }

    @Test
    void renderJsonToString_shouldThrowRuntimeExceptionOnError() throws JsonProcessingException {
        // Arrange
        RenderAccommodationAEM aem = new RenderAccommodationAEM();
        when(objectMapper.writeValueAsString(aem)).thenThrow(new JsonProcessingException("Error") {});

        // Act & Assert
        assertThrows(RuntimeException.class, () -> putAccommodationService.renderJsonToString(aem));
    }

    @Test
    void pathBuilderJson_shouldBuildCorrectPathForPut() {
        String result = putAccommodationService.pathBuilderJson(false, "hotel", "milan", "lombardy", "/base/path/");
        assertTrue(result.contains("destination_put"));
    }

    @Test
    void pathBuilderJson_shouldBuildCorrectPathForUpdate() {
        String result = putAccommodationService.pathBuilderJson(true, "hotel", "milan", "lombardy", "/base/path/");
        assertTrue(result.contains("destination_update_"));
    }

    @Test
    void pathBuilderJson_shouldTruncateLongNames() {
        String longName = "This is a very long hotel name that exceeds eighty characters and should be truncated for the path";
        String result = putAccommodationService.pathBuilderJson(false, longName, "Milan", "Lombardy", "/base/path/");
        assertTrue(result.contains("this is a very long hotel name that exceeds eighty characters and should be trunca"));
    }
}
